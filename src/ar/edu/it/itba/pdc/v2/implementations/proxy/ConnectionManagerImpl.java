package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.v2.ProxyData;
import ar.edu.it.itba.pdc.v2.implementations.configurator.ConnectionStatus;
import ar.edu.it.itba.pdc.v2.implementations.monitor.Monitor;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;
import ar.edu.it.itba.pdc.v2.interfaces.DataStorage;

public class ConnectionManagerImpl implements ConnectionManager {
	private Map<InetAddress, List<ConnectionStatus>> connections;
	private Logger connectionLog = Logger
			.getLogger(ConnectionManagerImpl.class);
	private DataStorage dataStorage;
	private ProxyData pd;

	public ConnectionManagerImpl(Monitor monitor, ProxyData pd) {
		connections = new HashMap<InetAddress, List<ConnectionStatus>>();
		connectionLog.setLevel(Level.INFO);
		this.dataStorage = monitor.getDataStorage();
		this.pd = pd;
	}

	public void run() {
		while (!Thread.interrupted()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// connectionLog.info("Cleaning closed sockets");
			Set<InetAddress> keys = connections.keySet();
			synchronized (connections) {
				for (InetAddress addr : keys) {
					List<ConnectionStatus> openConn = connections.get(addr);
					Iterator<ConnectionStatus> iter = openConn.iterator();
					while (iter.hasNext()) {
						ConnectionStatus cs = iter.next();
						Socket s = cs.getSocket();
						if ((s.isClosed() || !s.isConnected()) && !cs.isInUse()) {
							iter.remove();
						}
					}
				}
			}

		}
	}

	public Socket getConnection(String host) throws IOException,
			UnknownHostException {
		URL url = new URL("http://" + host);
		connectionLog.info("Requested connection for " + url.toString());
		InetAddress addr;
		if (pd.extistsIntermediateProxy()) {
			addr = pd.getIntermProxyAddr();
		} else {
			addr = InetAddress.getByName(url.getHost());
		}
		synchronized (connections) {

			List<ConnectionStatus> connectionList = connections.get(addr);
			if (connectionList == null) {
				connectionList = new LinkedList<ConnectionStatus>();
				connections.put(addr, connectionList);
			}
			for (ConnectionStatus connection : connectionList) {
				Socket s = connection.getSocket();
				if (!connection.isInUse() && !s.isClosed() && s.isConnected()
						&& !s.isInputShutdown() && !s.isOutputShutdown()) {
					connectionLog
							.info("Reused connection to " + url.toString());
					connection.takeConnection();
					return s;
				}
			}
		}
		connectionLog.info("Created new connection to " + url.toString());
		int port;
		if(pd.extistsIntermediateProxy()) {
			port = pd.getIntermProxyPort();
		} else {
			port = (url.getPort() == -1) ? 80 : url.getPort();
		}
		Socket s = null;
		try {
			s = new Socket(addr, port);
			dataStorage.addServerOpenConection(1);
		} catch (ConnectException e) {
			return null;
		}
		synchronized (connections) {

			connections.get(addr).add(new ConnectionStatus(s, true));
		}
		return s;

	}

	public void releaseConnection(Socket socket, boolean keepAlive) {
		List<ConnectionStatus> connectionList = connections.get(socket
				.getInetAddress());
		synchronized (connections) {
			for (ConnectionStatus connection : connectionList) {
				Socket s = connection.getSocket();
				if (equals(socket, s) && keepAlive) {
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

	private boolean equals(Socket s1, Socket s2) {
		return s1.getLocalPort() == s2.getLocalPort()
				&& s1.getLocalSocketAddress()
						.equals(s2.getLocalSocketAddress())
				&& s1.getPort() == s2.getPort()
				&& s1.getRemoteSocketAddress().equals(
						s2.getRemoteSocketAddress());
	}
}
