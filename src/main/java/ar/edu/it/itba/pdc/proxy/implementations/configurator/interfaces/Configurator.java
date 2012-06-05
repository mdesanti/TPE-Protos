package ar.edu.it.itba.pdc.proxy.implementations.configurator.interfaces;

import java.net.InetAddress;

import javax.ws.rs.core.MediaType;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;

public interface Configurator extends Runnable {

	/**
	 * @return True if the proxy must apply image rotation. False otherwise.
	 */
	public boolean applyRotationsFor(Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * @return True if the proxy must apply l33t text/plain transformations.
	 *         False otherwise.
	 */
	public boolean applyTextTransformationFor(Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * 
	 * @param addr
	 *            IP
	 * @return True if the proxy must block addr. False otherwise.
	 */
	public boolean isAccepted(InetAddress addr, Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * 
	 * @param mediaType
	 * @return True if the proxy must block a given mediaType. False otherwise.
	 */
	public boolean isAccepted(MediaType mediaType, Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * 
	 * @param url
	 * @return True if the proxy must block a given url. False otherwise.
	 */
	public boolean isAccepted(String url, Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * 
	 * @return The max size allowed by the proxy. Returns -1 it all sizes are
	 *         allowed.
	 */
	public int getMaxSize(Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * 
	 * @return True if a rotation or a text transformation must be done. False
	 *         otherwise.
	 */
	public boolean applyTransformationFor(Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * 
	 * @return True if the proxy must block everything.False otherwise.
	 */
	public boolean blockAll(Browser b, OperatingSystem os, InetAddress ip);

}
