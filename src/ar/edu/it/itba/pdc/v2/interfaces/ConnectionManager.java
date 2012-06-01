package ar.edu.it.itba.pdc.v2.interfaces;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;

public interface ConnectionManager {

	public Socket getConnection(String host) throws IOException;
	
	public void releaseConnection(Socket socket, boolean keepAlive);
	
}
