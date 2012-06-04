package ar.edu.it.itba.pdc.v2.interfaces;

import java.net.InetAddress;

import javax.ws.rs.core.MediaType;

public interface Configurator extends Runnable {

	/**
	 * @return True if the proxy must apply image rotation. False otherwise.
	 */
	public boolean applyRotations();

	/**
	 * @return True if the proxy must apply l33t text/plain transformations.
	 *         False otherwise.
	 */
	public boolean applyTextTransformation();

	/**
	 * 
	 * @param addr
	 *            IP
	 * @return True if the proxy must block addr. False otherwise.
	 */
	public boolean isAccepted(InetAddress addr);

	/**
	 * 
	 * @param mediaType
	 * @return True if the proxy must block a given mediaType. False otherwise.
	 */
	public boolean isAccepted(MediaType mediaType);

	/**
	 * 
	 * @param url
	 * @return True if the proxy must block a given url. False otherwise.
	 */
	public boolean isAccepted(String url);

	/**
	 * 
	 * @return The max size allowed by the proxy. Returns -1 it all sizes are
	 *         allowed.
	 */
	public int getMaxSize();

	/**
	 * 
	 * @return True if a rotation or a text transformation must be done. False
	 *         otherwise.
	 */
	public boolean applyTransformation();

	/**
	 * 
	 * @return True if the proxy must block everything.False otherwise.
	 */
	public boolean blockAll();

}
