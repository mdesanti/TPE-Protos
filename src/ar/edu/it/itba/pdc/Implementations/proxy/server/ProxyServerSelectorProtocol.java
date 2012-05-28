package ar.edu.it.itba.pdc.Implementations.proxy.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import ar.edu.it.itba.pdc.Implementations.proxy.TCPSelector;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;
import ar.edu.it.itba.pdc.v2.implementations.monitor.DataStorageImpl;
import ar.edu.it.itba.pdc.v2.implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;

public class ProxyServerSelectorProtocol implements TCPProtocol {

	private static int bufSize = 20 * 1024;
	public static Charset charset = Charset.forName("UTF-8");

	private Map<SocketChannel, Decoder> requestDecoders = new HashMap<SocketChannel, Decoder>();
	private Map<SocketChannel, Decoder> responseDecoders = new HashMap<SocketChannel, Decoder>();
	private ProxyWorker worker;
	private TCPSelector caller;
	private DataStorageImpl storage = DataStorageImpl.getInstance();

	public ProxyServerSelectorProtocol() {
	}

	@Override
	public void handleAccept(SelectionKey key) throws IOException {
		SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
		clntChan.configureBlocking(false); // Must be nonblocking to register
		// Register the selector with new channel for read and attach byte
		// buffer
		System.out.println(Calendar.getInstance().getTime().toString()
				+ "-> Connection accepted. Client address: "
				+ clntChan.socket().getInetAddress());
		requestDecoders.put(clntChan, new DecoderImpl(bufSize));
		responseDecoders.put(clntChan, new DecoderImpl(bufSize));
		clntChan.register(key.selector(), SelectionKey.OP_READ);
	}

	@Override
	public void handleRead(SelectionKey key) throws IOException {
		// Client socket channel has pending data
		SocketChannel clntChan = (SocketChannel) key.channel();
		ByteBuffer buf = ByteBuffer.allocate(bufSize);

		Decoder decoder = requestDecoders.get(clntChan);
		long bytesRead;
		try {
			bytesRead = clntChan.read(buf);
		} catch (IOException e) {
			return;
		}

		if (bytesRead == -1) { // Did the other end close?
			clntChan.close();
			requestDecoders.remove(clntChan);
		} else if (bytesRead > 0) {
			byte[] write = buf.array();
			decoder.decode(write, (int) bytesRead);
			// HTTPHeaders headers = decoder.getHeaders();
			// TODO: here we should analyze if the request is accepted by the
			// proxy
			storage.addTotalBytes(bytesRead);
			storage.addClientProxyBytes(bytesRead);

			System.out.println(Calendar.getInstance().getTime().toString()
					+ "-> Request from client to proxy. URL: "
					+ decoder.getHeader("RequestedURI") + "  Client address: "
					+ clntChan.socket().getInetAddress());
			boolean isMultipart = decoder.keepReading();
			worker.sendData(caller, clntChan, write, bytesRead, isMultipart);
			buf.clear();
			if (isMultipart) {
				key.interestOps(SelectionKey.OP_READ);
			} else {
				requestDecoders.put(clntChan, new DecoderImpl(bufSize));
			}
		}
	}

	@Override
	public void handleWrite(SelectionKey key,
			Map<SocketChannel, Queue<ByteBuffer>> map) throws IOException {

		ByteBuffer buf = map.get(key.channel()).peek();
		// buf.flip(); // Prepare buffer for writing
		SocketChannel clntChan = (SocketChannel) key.channel();
		Decoder decoder = responseDecoders.get(clntChan);
		if (buf == null || !clntChan.isConnected()) {
			clntChan.close();
			return;
		}

		decoder.decode(buf.array(), buf.array().length);
		decoder.applyRestrictions(buf.array(), buf.array().length);

		storage.addTotalBytes(buf.array().length);
		storage.addClientProxyBytes(buf.array().length);

		System.out.println(Calendar.getInstance().getTime().toString()
				+ "-> Response from proxy to client with status "
				+ decoder.getHeader("StatusCode") + ". Client address: "
				+ clntChan.socket().getInetAddress());
		try {
			clntChan.write(buf);
		} catch (IOException e) {
			System.out.println("Broken pipe");
			clntChan.close();
			map.get(key.channel()).remove();
			return;
		}
		// TODO: change condition. Shouldn't write any more if queue is empty
		if (!buf.hasRemaining()) { // Buffer completely written?
			map.get(key.channel()).remove();
			if (map.get(key.channel()).isEmpty() && !decoder.keepReading()) {
				// Nothing left, so no longer interested in writes
				key.interestOps(SelectionKey.OP_READ);
				responseDecoders.put(clntChan, new DecoderImpl(bufSize));
			} else if (!decoder.keepReading()) {
				// queue is not empty but decoder hasn't got anything to write
				key.interestOps(SelectionKey.OP_WRITE);
				responseDecoders.put(clntChan, new DecoderImpl(bufSize));
				buf.clear();
			} else {
				key.interestOps(SelectionKey.OP_WRITE);
			}
			buf.clear();
			buf.compact(); // Make room for more data to be read in
		} else {
			buf.compact();
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}

	public void setWorker(ProxyWorker worker) {
		this.worker = worker;
	}

	@Override
	public void setCaller(TCPSelector caller) {
		this.caller = caller;
	}
}
