package ar.edu.it.itba.pdc.proxy.implementations.monitor.implementations;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import ar.edu.it.itba.pdc.proxy.implementations.monitor.interfaces.DataStorage;

public class Monitor implements Runnable {

	private static int TIMEOUT = 500;
	private static int BUFFSIZE = 1024 * 5;
	private MonitorHandler protocol = new MonitorHandler(BUFFSIZE);
	private int port;
	
	public Monitor(int port) {
		this.port = port;
	}

	public void run() {
		try {
			Selector selector = null;
			selector = Selector.open();
			ServerSocketChannel listnChannel = ServerSocketChannel.open();
			listnChannel.socket().bind(new InetSocketAddress(port));
			listnChannel.configureBlocking(false); // must be nonblocking to
			listnChannel.register(selector, SelectionKey.OP_ACCEPT);
			while (true) { // Run forever, processing available I/O operations
				// Wait for some channel to be ready (or timeout)
				if (selector.select(TIMEOUT) == 0) { // returns # of ready chans
					continue;
				}
				// Get iterator on set of keys with I/O to process
				Iterator<SelectionKey> keyIter = selector.selectedKeys()
						.iterator();
				while (keyIter.hasNext()) {
					SelectionKey key = keyIter.next(); // Key is bit mask
					// Server socket channel has pending connection requests?
					if (key.isAcceptable()) {
						protocol.handleAccept(key);
					}
					// Client socket channel has pending data?
					if (key.isReadable()) {
						protocol.handleRead(key);
					}
					// Client socket channel is available for writing and
					// key is valid (i.e., channel not closed)?
					if (key.isValid() && key.isWritable()) {
						protocol.handleWrite(key);
					}
					keyIter.remove(); // remove from set of selected keys
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public DataStorage getDataStorage(){
		return protocol.getDataStorage();
	}
}
