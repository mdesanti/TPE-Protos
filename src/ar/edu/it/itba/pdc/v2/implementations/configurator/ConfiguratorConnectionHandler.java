package ar.edu.it.itba.pdc.v2.implementations.configurator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.v2.interfaces.ConfiguratorConnectionDecoderInt;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionHandler;

public class ConfiguratorConnectionHandler implements ConnectionHandler {

	private int maxMessageLength;
	private static String greeting = "Hello, please authenticate\n";
	private ConfiguratorConnectionDecoderInt decoder;
	private static Charset acceptedCharset = Charset.forName("ISO-8859-1");

	public ConfiguratorConnectionHandler(int maxMessageLength,
			ConfiguratorConnectionDecoderInt decoder) {
		this.maxMessageLength = maxMessageLength;
		this.decoder = decoder;
	}

	public void handle(Socket socket) throws IOException {
		Logger configHandler = Logger.getLogger("proxy.configurator.handler");
		configHandler.setLevel(Level.INFO);
		byte[] buffer = new byte[maxMessageLength];
		ByteBuffer cumBuffer = ByteBuffer.allocate(maxMessageLength);
		int receivedLength = 0, total = 0;
		String send = new String(greeting.getBytes(), acceptedCharset);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		boolean keepReading = true, firstTime = true;

		while (!decoder.closeConnection()) {
			if (firstTime) {
				os.write(send.getBytes());
				firstTime = false;
			}
			while (keepReading && (receivedLength = is.read(buffer)) != -1) {
				total += receivedLength;
				cumBuffer.put(buffer, cumBuffer.position(), receivedLength);
				keepReading = reachedEnd(buffer);
			}
			configHandler.info("Received line from client: " + new String(cumBuffer.array()).substring(0, total));

			String answer = decoder.decode(new String(cumBuffer.array())
					.substring(0, total));
			configHandler.info("Responding " + answer + " to client");

			os.write(answer.getBytes());
			cumBuffer.clear();
			keepReading = true;
			receivedLength = 0;
			total = 0;
		}
		configHandler.info("Client closed connection. Closing socket, reseting decoder and exiting handler");
		socket.close();
		decoder.reset();
	}

	private boolean reachedEnd(byte[] data) {
		String dataAsString = new String(data);
		return dataAsString.contains("\r\n");
	}

	public boolean applyRotations() {
		return decoder.applyRotations();
	}

	public boolean applyTextTransformation() {
		return decoder.applyTransformations();
	}

	public int getMaxSize() {
		return decoder.getMaxSize();
	}

	public boolean isAccepted(InetAddress addr) {
		Object[] set = decoder.getBlockedAddresses();
		for (Object blocked : set) {
			if (blocked.equals((InetAddress)addr))
				return false;
		}
		return true;
	}

	public boolean isAccepted(String str) {
		Object[] set = decoder.getBlockedURIs();
		for (Object blocked : set) {
			if (str.matches((String)blocked)) {
				return false;
			}
		}
		return true;
	}

	public boolean isAccepted(MediaType mtype) {
		Object[] set = decoder.getBlockedMediaType();
		for (Object blocked : set) {
			if (mtype.toString().equals((String)blocked)) {
				return false;
			}
		}
		return true;
	}

}
