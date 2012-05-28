package ar.edu.it.itba.pdc.v2.implementations.monitor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

import ar.edu.it.itba.pdc.Implementations.proxy.TCPSelector;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;

public class MonitorHandler {

	private int bufSize;
	private MonitorDecoder decoder = new MonitorDecoder();

	public MonitorHandler(int bufSize) {
		this.bufSize = bufSize;
	}

	public void handleAccept(SelectionKey key) throws IOException {

		SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
		clntChan.configureBlocking(false); // Must be nonblocking to register
		// Register the selector with new channel for read and attach byte
		// buffer
		ByteBuffer buf = ByteBuffer.allocate(bufSize);
		buf.put(new String("- Hello, for command list send \"help\"\n").getBytes());
		clntChan.register(key.selector(), SelectionKey.OP_WRITE, buf);

	}

	public void handleRead(SelectionKey key) throws IOException {
		SocketChannel clntChan = (SocketChannel) key.channel();
		ByteBuffer buf = (ByteBuffer) key.attachment();
		long bytesRead = clntChan.read(buf);
		if(bytesRead == -1) {
			clntChan.close();
			return;
		}
		String s = new String(buf.array());
		s = decoder.decode(s);
		ByteBuffer resp = ByteBuffer.allocate(bufSize);
		resp.put(s.getBytes());
		key.attach(resp);
		key.interestOps(SelectionKey.OP_WRITE);

	}

	public void handleWrite(SelectionKey key) throws IOException {

		ByteBuffer buf = (ByteBuffer) key.attachment();
		buf.flip(); // Prepare buffer for writing
		SocketChannel clntChan = (SocketChannel) key.channel();
		clntChan.write(buf);
		if (!buf.hasRemaining()) { // Buffer completely written?
			// Nothing left, so no longer interested in writes
			key.interestOps(SelectionKey.OP_READ);
		}
		buf.compact(); // Make room for more data to be read in

	}
}
