package ar.edu.it.itba.pdc.Implementations.proxy.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ar.edu.it.itba.pdc.Implementations.proxy.TCPSelector;
import ar.edu.it.itba.pdc.Implementations.proxy.utils.AttachmentImpl;
import ar.edu.it.itba.pdc.Implementations.proxy.utils.DataEvent;
import ar.edu.it.itba.pdc.Implementations.proxy.utils.DecoderImpl;
import ar.edu.it.itba.pdc.Interfaces.Decoder;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;

public class TCPClientSelector extends TCPSelector {

	private Map<SocketChannel, Decoder> decoders = new HashMap<SocketChannel, Decoder>();
	private Map<String, Socket> relations;
	// HashMap<SocketChannel, SocketChannel>();
	private List<Event> newEvents;

	public TCPClientSelector(ProxyWorker worker, int port, TCPProtocol protocol) {
		super(worker, port, protocol);
		newEvents = new LinkedList<Event>();
		relations = new HashMap<String, Socket>();
	}

	@Override
	public void run() {

		protocol.setCaller(this);

		// Create a selector to multiplex listening sockets and connections
		Selector selector = null;
		try {
			selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create a handler that will implement the protocol
		// Create listening socket channel for each port and register selector
		System.out.println("Proxy client initialized");
		while (true) { // Run forever, processing available I/O operations

			// Wait for some channel to be ready (or timeout)
			try {
				if (selector.select(TIMEOUT) == 0) { // returns # of ready chans
					// System.out.println(".....Client.....\n");
					getNewEvents(selector);
					for (Event e : newEvents) {
						e.process(selector);
					}
					newEvents.clear();
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

	private void getNewEvents(Selector selector) {
		synchronized (this.queue) {
			Iterator<DataEvent> changes = this.queue.iterator();
			while (changes.hasNext()) {
				DataEvent change = changes.next();
				Decoder decoder = new DecoderImpl(BUFSIZE);

				decoder.decode(change.getData(), change.getData().length);
				newEvents.add(new Event(decoder, change.getData(), change
						.getFrom(), change.isMulipart()));
				changes.remove();
			}
		}
	}

	class Event {

		private Decoder decoder;
		private byte[] data;
		private SocketChannel from;
		private boolean multipart = false;

		public Event(Decoder decoder, byte[] data, SocketChannel from,
				boolean isMultipart) {
			this.decoder = decoder;
			this.data = data;
			this.from = from;
			this.multipart = isMultipart;
		}

		public Decoder getDecoder() {
			return decoder;
		}

		void process(Selector selector) {

			SocketChannel chan = null;

			if (decoder.getHeader("RequestedURI") != null) {
				URL url = null;
				try {
					url = new URL("http://" + decoder.getHeader("Host"));
					Socket s = relations.get(url.getHost());
					if (s == null || !s.isConnected()) {
						InetAddress addr = InetAddress.getByName(decoder
								.getHeader("Host"));
						int port = url.getPort() == -1 ? url.getDefaultPort()
								: url.getPort();
						s = new Socket(addr, port);
					}

					chan = SocketChannel.open(s.getRemoteSocketAddress());
					while (!chan.finishConnect()) {
						System.out.print("."); // Do something else
					}
					relations.put(url.getHost(), chan.socket());

					chan.configureBlocking(false);
					SelectionKey k = chan.register(selector,
							SelectionKey.OP_WRITE);

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO should return error
					return;
				}
			} else {

			}

			SelectionKey key = chan.keyFor(selector);
			key.attach(new AttachmentImpl(multipart, from));
			if (!map.containsKey(chan))
				map.put(chan, new LinkedList<ByteBuffer>());
			ByteBuffer buf = ByteBuffer.wrap(data);
			map.get(chan).add(buf);
			key.interestOps(SelectionKey.OP_WRITE);
		}
	}

}