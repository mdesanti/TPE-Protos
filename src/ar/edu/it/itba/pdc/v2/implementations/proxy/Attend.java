package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.v2.implementations.monitor.Monitor;
import ar.edu.it.itba.pdc.v2.implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.v2.interfaces.Analyzer;
import ar.edu.it.itba.pdc.v2.interfaces.Configurator;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionHandler;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;

public class Attend implements Runnable {

	private Socket socket;
	private ConnectionHandler handler;
	private ConnectionManager connectionManager;
	private Analyzer analyzer;
	private Configurator configurator;
	private Monitor monitor;

	public Attend(Socket socket, ConnectionHandler handler,
			ConnectionManager connectionManager, Analyzer analyzer,
			Configurator configurator,Monitor monitor) {
		this.handler = handler;
		this.socket = socket;
		this.connectionManager = connectionManager;
		this.analyzer = analyzer;
		this.configurator = configurator;
		this.monitor = monitor;
	}

	public void run() {
		Logger attend = Logger.getLogger("proxy.server.attend");
		Decoder decoder = new DecoderImpl(20 * 1024);
		byte[] buffer = new byte[500];
		analyzer = new AnalyzerImp(connectionManager, configurator,monitor);
		ByteBuffer req = ByteBuffer.allocate(20 * 1024);
		String s = socket.getRemoteSocketAddress().toString();
		while (!socket.isClosed()) {
			try {
				int receivedMsg = 0, totalCount = 0;

				InputStream clientIs = socket.getInputStream();

				boolean keepReading = true;

				// read until headers are complete
				attend.debug("Before reading headers from client");
				attend.info("Reading headers from client");
				while (keepReading
						&& ((receivedMsg = clientIs.read(buffer)) != -1)) {
					totalCount += receivedMsg;
					req.put(buffer, 0, receivedMsg);
					keepReading = !decoder.completeHeaders(req.array(),
							req.array().length);
				}
				if (receivedMsg == -1) {
					attend.info("Received -1 from client. Closing connection");
					socket.close();
				}
				attend.debug("Headers completely read. Sending to analyzer");
				monitor.getDataStorage().addClientProxyBytes(totalCount);
				analyzer.analyze(req, totalCount, socket);
				if (!socket.isConnected() || socket.isClosed()
						|| !analyzer.keepConnection()) {
					attend.info("Analyzer returned. Closing socket");
					req.clear();
					socket.close();
				}

			} catch (IOException e) {
				return;
			}
		}

		// System.out.printf("Se desconecto %s\n", s);
	}

}
