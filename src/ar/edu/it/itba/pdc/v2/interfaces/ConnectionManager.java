package ar.edu.it.itba.pdc.v2.interfaces;

import java.net.Socket;

public interface ConnectionManager {

	public Socket getConnection(String host);
	
	public void releaseConnection(Socket socket);
	
}
