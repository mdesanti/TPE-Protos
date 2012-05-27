package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import ar.edu.it.itba.pdc.v2.implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.v2.interfaces.Analyzer;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionHandler;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;

public class Attend implements Runnable {

	private Socket socket;
	private ConnectionHandler handler;
	private ConnectionManager connectionManager;
	private Analyzer analyzer;

	public Attend(Socket socket, ConnectionHandler handler,
			ConnectionManager connectionManager, Analyzer analyzer) {
		this.handler = handler;
		this.socket = socket;
		this.connectionManager = connectionManager;
		this.analyzer = analyzer;
	}

	public void run() {

		Decoder decoder = new DecoderImpl(20 * 1024);
		byte[] buffer = new byte[500];
		analyzer = new AnalyzerImp(connectionManager);
		ByteBuffer req = ByteBuffer.allocate(20 * 1024);
		String s = socket.getRemoteSocketAddress().toString();
//		System.out.printf("Se conecto %s - Thread nro: %d\n", s, Thread.currentThread().getId());
		while (!socket.isClosed()) {
			try {
				int receivedMsg = 0, totalCount = 0;

				InputStream clientIs = socket.getInputStream();

				boolean keepReading = true;

				// read until headers are complete
				while (keepReading
						&& ((receivedMsg = clientIs.read(buffer)) != -1)) {
					totalCount += receivedMsg;
					req.put(buffer, 0, receivedMsg);
					keepReading = !decoder.completeHeaders(req.array(),
							req.array().length);
				}

				analyzer.analyze(req, totalCount, socket);

				socket.close();

			} catch (IOException e) {
			}
		}

//		System.out.printf("Se desconecto %s\n", s);
	}
}
