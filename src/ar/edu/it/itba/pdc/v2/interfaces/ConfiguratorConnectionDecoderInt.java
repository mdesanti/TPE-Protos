package ar.edu.it.itba.pdc.v2.interfaces;

import java.net.InetAddress;
import java.util.Set;

public interface ConfiguratorConnectionDecoderInt {

	public String decode(String s);
	
	public boolean closeConnection();
	
	public Set<InetAddress> getBlockedAddresses();
	
	public Set<String> getBlockedMediaType();
	
	public Set<String> getBlockedURIs();
	
	public int getMaxSize();
	
	public boolean applyRotations();
	
	public boolean applyTransformations();
}
