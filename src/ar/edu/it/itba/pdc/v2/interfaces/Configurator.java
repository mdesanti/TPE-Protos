package ar.edu.it.itba.pdc.v2.interfaces;

import java.net.InetAddress;

public interface Configurator {

	public boolean applyRotations();
	
	public boolean applyTextTransformation();
	
	public boolean isAccepted(InetAddress addr);
	
	public boolean isAccepted(String str);
	
	public int getMaxSize();
	
	
	
	
}
