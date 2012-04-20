package ar.edu.it.itba.pdc.Implementations;

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

import ar.edu.it.itba.pdc.Interfaces.Decoder;
import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;

public class ProxySelectorProtocol implements TCPProtocol {

	private int bufSize;
	public static Charset charset = Charset.forName("UTF-8");
	private Map<SocketChannel, Decoder> decoders = new HashMap<SocketChannel, Decoder>();
	private Map<SocketChannel, SocketChannel> relations = new HashMap<SocketChannel, SocketChannel>();

	public ProxySelectorProtocol(int bufSize) {
		this.bufSize = bufSize;
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
			SocketChannel dest;
			SocketChannel src = clntChan;
			URL url = new URL(decoder.getHeader("Host"));
			if (headers.isResponse()) {
				dest = relations.get(clntChan);
			} else {
				if (relations.get(clntChan) != null)
					dest = relations.get(clntChan);
				else {
					dest = SocketChannel.open(new InetSocketAddress(InetAddress
							.getByName(url.getHost()),
							url.getPort() == -1 ? url.getDefaultPort() : url
									.getPort()));
					dest.configureBlocking(false);

					while (!dest.finishConnect()) {
						System.out.print("."); // Do something else
					}

					relations.put(dest, src);
					relations.put(src, dest);
				}
			}
//			Si pongo esta linea y comento todo lo que esta por debajo al nc le llega el request
//			dest.write(ByteBuffer.wrap(write));

			// register
			// Register selector with channel. The returned key is ignored
			dest.register(key.selector(), SelectionKey.OP_WRITE,
					ByteBuffer.wrap(write));
			src.register(key.selector(), SelectionKey.OP_READ, buf.clear());

		}
	}

	@Override
	public void handleWrite(SelectionKey key) throws IOException {
		ByteBuffer buf = (ByteBuffer) key.attachment();
		buf.flip(); // Prepare buffer for writing
		SocketChannel clntChan = (SocketChannel) key.channel();
		// System.out.println("Escribo al cliente: " + new String(buf.array()));
		System.out.println("Escribo: " + new String(buf.array()));
		clntChan.write(buf);
		if (!buf.hasRemaining()) { // Buffer completely written?
			// Nothing left, so no longer interested in writes
			key.interestOps(SelectionKey.OP_READ);
		}
		buf.compact(); // Make room for more data to be read in
		buf.clear();
	}
}
