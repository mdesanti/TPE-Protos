package ar.edu.it.itba.pdc.v2.interfaces;

import java.net.InetAddress;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

public interface ConfiguratorConnectionDecoderInt {

	public String decode(String s);
	
	public boolean closeConnection();
	
	public Set<InetAddress> getBlockedAddresses();
	
	public Set<MediaType> getBlockedMediaType();
	
	public Set<Pattern> getBlockedURIs();
	
	public int getMaxSize();
	
	public boolean applyRotations();
	
	public boolean applyTransformations();
}
