package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ar.edu.it.itba.pdc.v2.implementations.utils.ConnectionStatus;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;

public class ConnectionManagerImpl implements ConnectionManager{

	private Map<String, ConnectionStatus> connections;
	
	public ConnectionManagerImpl() {
		connections = new HashMap<String, ConnectionStatus>();
	}
	
	public synchronized Socket getConnection(String host) {
		try {
			URL url = new URL("http://" + host);
			ConnectionStatus connection = connections.get(url.getHost());
//			if(connection != null && connection.isInUse())
//				return null;
//			if(connection != null) {
//				if(connection.getSocket().isConnected() && !connection.getSocket().isClosed())
//					return connection.getSocket();
//			}
			int port = (url.getPort() == -1) ?80:url.getPort();
			Socket s = new Socket(InetAddress.getByName(url.getHost()), port);
//			connections.put(host, new ConnectionStatus(s, true));
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
	
	public synchronized void releaseConnection(Socket socket) {
//		connections.get(socket.getInetAddress().getHostName()).releaseConnection();
		
	}
	
}
