package ar.edu.it.itba.pdc.proxy.implementations.configurator.interfaces;

import java.net.InetAddress;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;

public interface ConfiguratorConnectionDecoderInt {

	/**
	 * Analyze an input string according to the Configurator protocol and
	 * returns a status code and message
	 * 
	 * @param s
	 * @return
	 */
	public String decode(String s);

	public boolean closeConnection();

	public void reset();

	/**
	 * Returns an array with the blocked ips.
	 * 
	 * @return
	 */
	public Object[] getBlockedAddressesFor(Browser b);
	public Object[] getBlockedAddressesFor(OperatingSystem os);
	public Object[] getBlockedAddressesFor(InetAddress addr);

	/**
	 * Returns an array with the blocked media types.
	 * 
	 * @return
	 */
	public Object[] getBlockedMediaTypeFor(Browser b);
	public Object[] getBlockedMediaTypeFor(OperatingSystem os);
	public Object[] getBlockedMediaTypeFor(InetAddress addr);

	/**
	 * Returns an array with the blocked uris.
	 * 
	 * @return
	 */
	public Object[] getBlockedURIsFor(Browser b);
	public Object[] getBlockedURIsFor(OperatingSystem os);
	public Object[] getBlockedURIsFor(InetAddress addr);

	/**
	 * 
	 * @return The max size allowed by the proxy. Returns -1 it all sizes are
	 *         allowed.
	 */
	public int getMaxSizeFor(Browser b);
	public int getMaxSizeFor(OperatingSystem os);
	public int getMaxSizeFor(InetAddress addr);

	/**
	 * @return True if the proxy must apply image rotation. False otherwise.
	 */
	public boolean applyRotationsFor(Browser b);
	public boolean applyRotationsFor(OperatingSystem os);
	public boolean applyRotationsFor(InetAddress addr);

	/**
	 * @return True if the proxy must apply l33t text/plain transformations.
	 *         False otherwise.
	 */
	public boolean applyTransformationsFor(Browser b);
	public boolean applyTransformationsFor(OperatingSystem os);
	public boolean applyTransformationsFor(InetAddress addr);

	/**
	 * 
	 * @return True if the proxy must block everything.False otherwise.
	 */
	public boolean blockAllFor(Browser b);
	public boolean blockAllFor(OperatingSystem os);
	public boolean blockAllFor(InetAddress addr);
}
