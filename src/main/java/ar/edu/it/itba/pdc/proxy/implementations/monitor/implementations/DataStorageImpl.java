package ar.edu.it.itba.pdc.proxy.implementations.monitor.implementations;

import ar.edu.it.itba.pdc.proxy.implementations.monitor.interfaces.DataStorage;

public class DataStorageImpl implements DataStorage {

	private long clientToProxyBytes = 0;
	private long proxyToServersBytes = 0;
	private long blocks = 0;
	private long transformations = 0;
	private long clientOpenConnections = 0;
	private long serversOpenConnections = 0;
	private static DataStorageImpl INSTANCE = new DataStorageImpl();

	public long getTotalBytes() {
		return clientToProxyBytes + proxyToServersBytes;
	}

	public long getClientProxyBytes() {

		return clientToProxyBytes;
	}

	public long getProxyServersBytes() {
		return proxyToServersBytes;
	}

	public long getBlocks() {
		return blocks;
	}

	public long getTransformations() {
		return transformations;
	}

	public long getClientOpenConnections() {
		return clientOpenConnections;
	}

	public long getServersOpenConnections() {
		return serversOpenConnections;
	}

	public static DataStorageImpl getInstance() {
		return INSTANCE;
	}

	public synchronized void addClientProxyBytes(long bytes) {
		clientToProxyBytes += bytes;
	}

	public synchronized void addProxyServerBytes(long bytes) {
		proxyToServersBytes += bytes;
	}

	public synchronized void addBlock() {
		this.blocks++;
	}

	public synchronized void addTransformation() {
		this.transformations++;
	}

	public synchronized void addClientOpenConnection(long clientOpenConnections) {
		this.clientOpenConnections += clientOpenConnections;
	}

	public synchronized void addServerOpenConnection(long serverOpenConnection) {
		this.serversOpenConnections += serverOpenConnection;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
