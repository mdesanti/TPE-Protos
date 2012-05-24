package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;

public class ConnectionManagerImpl implements ConnectionManager {

	@Override
	public Socket getConnection(String host) {
		try {
			URL url = new URL("http://" + host);
			int port = (url.getPort() == -1) ?80:url.getPort();
			Socket s = new Socket(InetAddress.getByName(url.getHost()), port);
			return s;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
