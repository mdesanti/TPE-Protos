package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ar.edu.it.itba.pdc.v2.implementations.utils.ConnectionStatus;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;

public class ConnectionManagerImpl implements ConnectionManager {

	private Map<InetAddress, List<ConnectionStatus>> connections;

	public ConnectionManagerImpl() {
		connections = new HashMap<InetAddress, List<ConnectionStatus>>();
	}

	public synchronized Socket getConnection(String host) throws IOException {
			URL url = new URL("http://" + host);
			InetAddress addr = InetAddress.getByName(url.getHost());
			List<ConnectionStatus> connectionList = connections.get(addr);
			if(connectionList == null) {
				connectionList = new LinkedList<ConnectionStatus>();
				connections.put(addr, connectionList);
			}
			for(ConnectionStatus connection: connectionList) {
				Socket s = connection.getSocket();
				if(!connection.isInUse() && !s.isClosed() && s.isConnected()) {
					connection.takeConnection();
					return s;
				}
			}
			int port = (url.getPort() == -1) ? 80 : url.getPort();
			Socket s = new Socket(addr, port);
			connections.get(addr).add(new ConnectionStatus(s, true));
			return s;

	}

	public synchronized void releaseConnection(Socket socket, boolean keepAlive) {
		if(socket == null) {
			System.out.println("PTM");
		}
		List<ConnectionStatus> connectionList = connections.get(socket.getInetAddress());
		for(ConnectionStatus connection: connectionList) {
			Socket s = connection.getSocket();
			if(socket.equals(s) && keepAlive) {
				connection.releaseConnection();
				return;
			} else if (socket.equals(s)) {
				try {
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
