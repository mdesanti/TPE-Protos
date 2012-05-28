package ar.edu.it.itba.pdc.v2.implementations.configurator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import ar.edu.it.itba.pdc.v2.interfaces.ConnectionDecoder;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionHandler;

public class ConfiguratorConnecionHandler implements ConnectionHandler {

	private int maxMessageLength;
	private static String greeting = "Hello, please authenticate\n";
	private ConnectionDecoder decoder;
	private static Charset acceptedCharset = Charset.forName("ISO-8859-1");

	public ConfiguratorConnecionHandler(int maxMessageLength,
			ConnectionDecoder decoder) {
		this.maxMessageLength = maxMessageLength;
		this.decoder = decoder;
	}

	@Override
	public void handle(Socket socket) throws IOException {
		byte[] buffer = new byte[maxMessageLength];
		ByteBuffer cumBuffer = ByteBuffer.allocate(maxMessageLength);
		int receivedLength = 0;
		String send = new String(greeting.getBytes(), acceptedCharset);
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		boolean keepReading = true;

		while (!decoder.closeConnection()) {
			os.write(send.getBytes());

			while (keepReading && (receivedLength += is.read(buffer)) != -1) {
				cumBuffer.put(buffer, cumBuffer.position(), receivedLength);
				keepReading = reachedEnd(buffer);
			}

			String answer = decoder.decode(new String(cumBuffer.array(),
					acceptedCharset));

			os.write(answer.getBytes());
			cumBuffer.clear();
			keepReading = true;
		}
	}

	private boolean reachedEnd(byte[] data) {
		String dataAsString = new String(data, acceptedCharset);
		return dataAsString.contains("\r\n");
	}

}
