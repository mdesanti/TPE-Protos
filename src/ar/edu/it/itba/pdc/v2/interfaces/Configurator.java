package ar.edu.it.itba.pdc.v2.interfaces;

import java.net.InetAddress;

import javax.ws.rs.core.MediaType;

public interface Configurator extends Runnable {

	public boolean applyRotations();
	
	public boolean applyTextTransformation();
	
	public boolean isAccepted(InetAddress addr);
	
	public boolean isAccepted(MediaType str);
	
	public boolean isAccepted(String url);
	
	public int getMaxSize();
	
	public boolean applyTransformation();
	
	
	
	
}
