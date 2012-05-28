package ar.edu.it.itba.pdc.v2.implementations.monitor;

import ar.edu.it.itba.pdc.v2.interfaces.ConnectionDecoder;

public class MonitorDecoder implements ConnectionDecoder {
	
	private final String invalidParams = "Invalid parameters\n";
	
	public MonitorDecoder() {
	}

	public String decode(String s) {
		return null;
	}
	
	public boolean closeConnection() {
		return false;
	}
	
}
