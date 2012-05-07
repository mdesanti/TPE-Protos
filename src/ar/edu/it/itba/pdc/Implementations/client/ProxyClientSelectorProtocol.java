package ar.edu.it.itba.pdc.Implementations.client;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

import ar.edu.it.itba.pdc.Implementations.TCPSelector;
import ar.edu.it.itba.pdc.Implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.Interfaces.Decoder;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;

public class ProxyClientSelectorProtocol implements TCPProtocol {

	private static int bufSize = 40 * 1024;
	public static Charset charset = Charset.forName("UTF-8");

	private Map<SocketChannel, Decoder> decoders = new HashMap<SocketChannel, Decoder>();
	private ProxyWorker worker;
	private TCPSelector caller;
	private BufferedWriter logger;

	public ProxyClientSelectorProtocol() {
		try {
			FileWriter logger = new FileWriter(
					"/Users/mdesanti90/log/clientLog");
			this.logger = new BufferedWriter(logger);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handleAccept(SelectionKey key) throws IOException {
		SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
		clntChan.configureBlocking(false); // Must be nonblocking to register
		// Register the selector with new channel for read and attach byte
		// buffer
		decoders.put(clntChan, new DecoderImpl(bufSize));
		clntChan.register(key.selector(), SelectionKey.OP_READ,
				ByteBuffer.allocate(bufSize));
	}

	@Override
	public void handleRead(SelectionKey key) throws IOException {
		// Client socket channel has pending data
		SocketChannel clntChan = (SocketChannel) key.channel();
		ByteBuffer buf = ByteBuffer.allocate(bufSize);

		// Get the decoder that parsed the request
		Decoder decoder = decoders.get((SocketChannel) key.attachment());
		
		if (decoder == null) {
			System.out.println("NO DEBERIA PASAR - CLIENT - DECODER NULL - HANDLEREAD \n\n\n\n\n\n\n\n");
		}
		long bytesRead = -1;
		try {
			bytesRead = clntChan.read(buf);
		} catch (IOException e) {
			clntChan.close();
			return;
		}
		decoder.decode(buf.array(), (int)bytesRead);
		if (bytesRead == -1) { // Did the other end close?
			clntChan.close();
		} else if (bytesRead > 0) {
			// decoder.decode(buf.array(), (int) bytesRead);
			byte[] write = buf.array();
			// HTTPHeaders headers = decoder.getHeaders();
			// TODO: here we should analyze if the request is accepted by the
			// proxy
			System.out
					.println(Calendar.getInstance().getTime().toString()
							+ "-> Response from external server to proxy. Server address: "
							+ clntChan.socket().getInetAddress());
			worker.sendData(caller, (SocketChannel) key.attachment(), write,
					bytesRead);
			// if (decoder.keepReading()) {
			key.interestOps(SelectionKey.OP_READ);
			// }
		}
	}

	@Override
	public void handleWrite(SelectionKey key,
			Map<SocketChannel, Queue<ByteBuffer>> map) throws IOException {
		// TODO: peek, do not remove. In case the buffer can not be completely
		// written
		ByteBuffer buf = map.get(key.channel()).peek();
		// buf.flip(); // Prepare buffer for writing
		SocketChannel clntChan = (SocketChannel) key.channel();
		clntChan.write(buf);
		//The same decoder is used for request and response
		Decoder d = new DecoderImpl(bufSize);
		d.decode(buf.array(), buf.remaining());
		decoders.put((SocketChannel)key.attachment(), d);
		
		System.out
				.println(Calendar.getInstance().getTime().toString()
						+ "-> Request from proxy server to external server. Server address: "
						+ clntChan.socket().getInetAddress());
		// TODO: change condition. Shouldn't write any more if queue is empty
		if (!buf.hasRemaining()) { // Buffer completely written?
			map.get(key.channel()).remove();
			if (map.get(key.channel()).isEmpty()) {
				// Nothing left, so no longer interested in writes
				key.interestOps(SelectionKey.OP_READ);
			} else {
				key.interestOps(SelectionKey.OP_WRITE);
				buf.clear();
			}
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
