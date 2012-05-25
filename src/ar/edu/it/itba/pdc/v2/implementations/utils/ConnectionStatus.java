package ar.edu.it.itba.pdc.v2.implementations.utils;

import java.net.Socket;

public class ConnectionStatus {
	
	private Socket socket;
	private boolean inUse;
	
	public ConnectionStatus(Socket socket, boolean inUse) {
		this.socket = socket;
		this.inUse = inUse;
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

}
