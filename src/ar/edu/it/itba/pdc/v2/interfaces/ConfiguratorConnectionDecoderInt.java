package ar.edu.it.itba.pdc.v2.interfaces;

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
	public Object[] getBlockedAddresses();

	/**
	 * Returns an array with the blocked media types.
	 * 
	 * @return
	 */
	public Object[] getBlockedMediaType();

	/**
	 * Returns an array with the blocked uris.
	 * 
	 * @return
	 */
	public Object[] getBlockedURIs();

	/**
	 * 
	 * @return The max size allowed by the proxy. Returns -1 it all sizes are
	 *         allowed.
	 */
	public int getMaxSize();

	/**
	 * @return True if the proxy must apply image rotation. False otherwise.
	 */
	public boolean applyRotations();

	/**
	 * @return True if the proxy must apply l33t text/plain transformations.
	 *         False otherwise.
	 */
	public boolean applyTransformations();

	/**
	 * 
	 * @return True if the proxy must block everything.False otherwise.
	 */
	public boolean blockAll();
}
