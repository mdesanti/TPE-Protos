package ar.edu.it.itba.pdc.proxy.interfaces;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionManager extends Runnable {

	public Socket getConnection(String host) throws IOException;
	
	public void releaseConnection(Socket socket, boolean keepAlive);
	
	public void cleanAll(Socket socket);
	
}
