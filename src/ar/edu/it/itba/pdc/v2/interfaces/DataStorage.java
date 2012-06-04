package ar.edu.it.itba.pdc.v2.interfaces;

public interface DataStorage {

	/**
	 * Returns total bytes transfered by the proxy.
	 * 
	 * @return
	 */
	public long getTotalBytes();

	/**
	 * Returns bytes transfered from the client to the proxy server.
	 * 
	 * @return
	 */
	public long getClientProxyBytes();

	/**
	 * Adds bytes transfered from the client to the proxy server.
	 * 
	 * @param bytes
	 */
	public void addClientProxyBytes(long bytes);

	/**
	 * Returns bytes transfered from the proxy client to the origin server.
	 * 
	 * @return
	 */
	public long getProxyServersBytes();

	/**
	 * Adds bytes transfered from the proxy client to the origin server.
	 * 
	 * @param bytes
	 */
	public void addProxyServerBytes(long bytes);

	/**
	 * Returns the amount of block actions made by the proxy.
	 * 
	 * @return
	 */
	public long getBlocks();

	/**
	 * Increase the amount of block actions made by the proxy.
	 */
	public void addBlock();

	/**
	 * Returns the amount of transformations actions made by the proxy.
	 * 
	 * @return
	 */
	public long getTransformations();

	/**
	 * Increase the amount of transformations actions made by the proxy.
	 */
	public void addTransformation();

	/**
	 * Returns the amount of client-Proxy server open connections.
	 * 
	 * @return
	 */
	public long getClientOpenConnections();

	/**
	 * Adds client-proxy server open connections.
	 * 
	 * @param clientOpenConnections
	 */
	public void addClientOpenConnection(long clientOpenConnections);

	/**
	 * Returns the amount of proxy client - origin server open connections.
	 * 
	 * @return
	 */
	public long getServersOpenConnections();

	/**
	 * Adds proxy client - origin server open connections.
	 * 
	 * @param serverOpenConection
	 */
	public void addServerOpenConnection(long serverOpenConection);
}
