package ar.edu.it.itba.pdc.Implementations.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import ar.edu.it.itba.pdc.Implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.Interfaces.Decoder;
import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;

public class ProxyServerSelectorProtocol implements TCPProtocol {

	private static int bufSize = 10*1024;
	public static Charset charset = Charset.forName("UTF-8");
	
	private Map<SocketChannel, Decoder> decoders = new HashMap<SocketChannel, Decoder>();
	private ProxyWorker worker;
	private TCPServerSelector caller;

	public ProxyServerSelectorProtocol() {
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
		ByteBuffer buf = (ByteBuffer) key.attachment();

		Decoder decoder = decoders.get(clntChan);
		long bytesRead = clntChan.read(buf);

		if (bytesRead == -1) { // Did the other end close?
			clntChan.close();
		} else if (bytesRead > 0) {
			decoder.decode(buf.array());
			byte[] write = buf.array();
			HTTPHeaders headers = decoder.getHeaders();
			//TODO: here we should analyze if the request is accepted by the proxy
			worker.sendData(caller, clntChan, write, bytesRead);
			System.out.println("Mande data al worker");
			key.interestOps(SelectionKey.OP_READ);
		}
	}

	@Override
	public void handleWrite(SelectionKey key, Map<SocketChannel, Queue<ByteBuffer>> map) throws IOException {
		//TODO: peek, do not remove. In case the buffer can not be completely written
		ByteBuffer buf = map.get(key.channel()).peek();
//		buf.flip(); // Prepare buffer for writing
		System.out.println(buf.hasRemaining());
		SocketChannel clntChan = (SocketChannel) key.channel();
		System.out.println("Escribo al cliente: " + new String(buf.array()));
//		System.out.println("Escribo: " + new String(buf.array()));
		System.out.println("Escribo " + clntChan.write(buf) + "bytes");
		if(buf.hasRemaining()) {
			System.out.println("tiene remaining");
		}
		//TODO: change condition. Shouldn't write any more if queue is empty
		if (!buf.hasRemaining()) { // Buffer completely written?
			// Nothing left, so no longer interested in writes
			key.interestOps(SelectionKey.OP_READ);
			map.get(key.channel()).remove();
			buf.compact(); // Make room for more data to be read in
			buf.clear();
		} else {
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}
	
	public void setWorker(ProxyWorker worker) {
		this.worker = worker;
	}
	
	public void setCaller(TCPServerSelector caller) {
		this.caller = caller;
	}
}
