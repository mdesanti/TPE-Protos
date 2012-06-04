package ar.edu.it.itba.pdc.proxy.implementations.configurator.implementations;

import java.net.Socket;

public class ConnectionStatus {
	
	private Socket socket;
	private boolean inUse;
	private long time;
	
	public ConnectionStatus(Socket socket, boolean inUse, long time) {
		this.socket = socket;
		this.inUse = inUse;
		this.time = time;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public boolean isInUse() {
		return inUse;
	}
	
	public void releaseConnection() {
		this.inUse = false;
	}
	
	public void takeConnection() {
		this.inUse = true;
	}
	
	public long getTime() {
		return time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((socket == null) ? 0 : socket.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionStatus other = (ConnectionStatus) obj;
		if (socket == null) {
			if (other.socket != null)
				return false;
		} else if (!equals(other.socket, socket))
			return false;
		return true;
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
