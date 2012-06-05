package ar.edu.it.itba.pdc.proxy.implementations.monitor.implementations;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.proxy.implementations.monitor.exceptions.BadCredentialException;
import ar.edu.it.itba.pdc.proxy.implementations.monitor.interfaces.DataStorage;

public class MonitorHandler {

	private int bufSize;
	private MonitorDecoder decoder = new MonitorDecoder();
	private Logger configLog;

	public MonitorHandler(int bufSize) {
		configLog= Logger.getLogger(this.getClass());
		configLog.setLevel(Level.INFO);
		this.bufSize = bufSize;
	}

	public void handleAccept(SelectionKey key) throws IOException {

		SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
		clntChan.configureBlocking(false); // Must be nonblocking to register
		// Register the selector with new channel for read and attach byte
		// buffer
		ByteBuffer buf = ByteBuffer.allocate(bufSize);
		buf.put(("- Hello, please identify yoursefl (user:password) \n")
				.getBytes());
		MonitorAttach att = new MonitorAttach(buf);
		clntChan.register(key.selector(), SelectionKey.OP_WRITE, att);

	}

	public void handleRead(SelectionKey key) throws IOException {
		SocketChannel clntChan = (SocketChannel) key.channel();
		MonitorAttach att = (MonitorAttach) key.attachment();
		boolean logged = att.getLogged();
		ByteBuffer buf = att.getBuffer();
		long bytesRead = clntChan.read(buf);
		if (bytesRead == -1) {
			clntChan.close();
			return;
		}
		String s = new String(buf.array(), 0, buf.position());
		try {
			if (!logged) {
				s = decoder.logIn(s);
				att.logIn();
			} else{
				s = decoder.decode(s);
				if(s == null){
					clntChan.close();
					return;
				}
			}
		} catch (BadCredentialException e) {
			configLog.info("Log in failed.");
			s = "User or password are incorrect, please try again\n";
		}
		buf.clear();
		buf.put(s.getBytes());
		key.attach(att);
		key.interestOps(SelectionKey.OP_WRITE);

	}

	public void handleWrite(SelectionKey key) throws IOException {

		MonitorAttach att = (MonitorAttach) key.attachment();
		ByteBuffer buf = att.getBuffer();
		buf.flip(); // Prepare buffer for writing
		SocketChannel clntChan = (SocketChannel) key.channel();
		clntChan.write(buf);
		if (!buf.hasRemaining()) { // Buffer completely written?
			// Nothing left, so no longer interested in writes
			key.interestOps(SelectionKey.OP_READ);
		}
		buf.clear();
		// buf.compact(); // Make room for more data to be read in

	}
	
	public DataStorage getDataStorage(){
		return decoder.getStorage();
	}
}
