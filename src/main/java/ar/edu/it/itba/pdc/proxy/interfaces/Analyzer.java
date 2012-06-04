package ar.edu.it.itba.pdc.proxy.interfaces;

import java.net.Socket;
import java.nio.ByteBuffer;

public interface Analyzer {

	/**
	 * Analyzes a client request and a server response.
	 * 
	 * @param buffer
	 *            Buffer containing the request headers.
	 * @param count
	 *            The buffer real length.
	 * @param socket
	 *            Socket between the client and the proxy server.
	 */
	public void analyze(ByteBuffer buffer, int count, Socket socket);

	/**
	 * 
	 * @return true if the connection between the client and the proxy server
	 *         must keep alive. False if it must close
	 */
	public boolean keepConnection();

}
