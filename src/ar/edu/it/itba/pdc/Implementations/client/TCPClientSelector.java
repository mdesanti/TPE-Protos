package ar.edu.it.itba.pdc.Implementations.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import ar.edu.it.itba.pdc.Implementations.TCPSelector;
import ar.edu.it.itba.pdc.Implementations.utils.DataEvent;
import ar.edu.it.itba.pdc.Implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.Interfaces.Decoder;
import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;

public class TCPClientSelector extends TCPSelector {

	private Map<SocketChannel, Decoder> decoders = new HashMap<SocketChannel, Decoder>();
	private Map<SocketChannel, SocketChannel> relations = new HashMap<SocketChannel, SocketChannel>();

	public TCPClientSelector(ProxyWorker worker, int port, TCPProtocol protocol) {
		super(worker, port, protocol);
	}

	@Override
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
		System.out.println("Proxy client listening on port " + port);
		while (true) { // Run forever, processing available I/O operations

			synchronized (this.queue) {
				Iterator<DataEvent> changes = this.queue.iterator();
				while (changes.hasNext()) {
					DataEvent change = changes.next();
					Decoder decoder = decoders.get(change.getFrom());
					HTTPHeaders headers;
					if (decoder == null) {
						decoder = new DecoderImpl(BUFSIZE);
//						decoders.put(change.getFrom(), decoder);
					}
					decoder.decode(change.getData(), change.getData().length);
					headers = decoder.getHeaders();
					// not the first
					SocketChannel chan = relations.get(change.getFrom());
					// first packet from server
					if (headers.getHeader("RequestedURI") != null) {
						URL url = null;
						try {
							url = new URL("http://" + decoder.getHeader("Host"));
							chan = SocketChannel.open(new InetSocketAddress(
									InetAddress.getByName(decoder.getHeader("Host")), url
											.getPort() == -1 ? url
											.getDefaultPort() : url.getPort()));
							chan.configureBlocking(false);
							while (!chan.finishConnect()) {
								System.out.print("."); // Do something else
							}

							SelectionKey k = chan.register(selector, SelectionKey.OP_WRITE);
							k.attach(change.getFrom());

						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {

					}

					SelectionKey key = chan.keyFor(selector);
					if (!map.containsKey(chan))
						map.put(chan, new LinkedList<ByteBuffer>());
					ByteBuffer buf = ByteBuffer.wrap(change.getData());
					map.get(chan).add(buf);
					key.interestOps(SelectionKey.OP_WRITE);
					changes.remove();
				}
			}

			// Wait for some channel to be ready (or timeout)
			try {
				if (selector.select(TIMEOUT) == 0) { // returns # of ready chans
				// System.out.print(".");
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