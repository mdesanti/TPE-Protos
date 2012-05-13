package ar.edu.it.itba.pdc.Implementations.proxy.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

import ar.edu.it.itba.pdc.Implementations.proxy.TCPSelector;
import ar.edu.it.itba.pdc.Implementations.proxy.utils.AttachmentImpl;
import ar.edu.it.itba.pdc.Implementations.proxy.utils.DataEvent;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;

public class TCPServerSelector extends TCPSelector {

	public TCPServerSelector(ProxyWorker worker, int port, TCPProtocol protocol) {
		super(worker, port, protocol);
	}

	public void run() {

		protocol.setCaller(this);

		// Create a selector to multiplex listening sockets and connections
		Selector selector = null;
		ServerSocketChannel listnChannel = null;
		try {
			selector = Selector.open();
			listnChannel = ServerSocketChannel.open();
			listnChannel.socket().bind(new InetSocketAddress(port));
			listnChannel.configureBlocking(false); // must be nonblocking to
			// register
			// Register selector with channel. The returned key is ignored
			listnChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create a handler that will implement the protocol
		// Create listening socket channel for each port and register selector
		System.out.println("Proxy server listening on port " + port);
		while (true) { // Run forever, processing available I/O operations

			// Wait for some channel to be ready (or timeout)
			try {
				if (selector.select(TIMEOUT) == 0) { // returns # of ready chans
//					System.out.println(".....Server.....\n");
					synchronized (this.queue) {
						Iterator<DataEvent> changes = this.queue.iterator();
						while (changes.hasNext()) {
							DataEvent change = changes.next();
							SelectionKey key = change.getFrom().keyFor(selector);
							if (key != null) {
								if (!map.containsKey(change.getFrom()))
									map.put(change.getFrom(),
											new LinkedList<ByteBuffer>());
								map.get(change.getFrom()).add(
										ByteBuffer.wrap(change.getData()));
								key.interestOps(SelectionKey.OP_WRITE);
								key.attach(new AttachmentImpl(change.isMulipart(), null));
							}
							changes.remove();
						}
					}
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
						protocol.handleWrite(key, this.map);
					}
					keyIter.remove(); // remove from set of selected keys
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}