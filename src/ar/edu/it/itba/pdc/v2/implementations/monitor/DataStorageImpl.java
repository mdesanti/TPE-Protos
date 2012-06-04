package ar.edu.it.itba.pdc.v2.implementations.monitor;

import ar.edu.it.itba.pdc.v2.interfaces.DataStorage;

public class DataStorageImpl implements DataStorage {

	private long clientToProxyBytes;
	private long proxyToServersBytes;
	private long blocks;
	private long transformations;
	private long clientOpenConections;
	private long serversOpenConections;
	private static DataStorageImpl INSTANCE = new DataStorageImpl();

	private DataStorageImpl() {
		this.clientToProxyBytes = 0;
		this.proxyToServersBytes = 0;
		this.blocks = 0;
		this.transformations = 0;
		this.clientOpenConections = 0;
		this.serversOpenConections = 0;
	}

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
		return clientOpenConections;
	}

	public long getServersOpenConnections() {
		return serversOpenConections;
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
		clientOpenConnections += clientOpenConnections;
	}

	public synchronized void addServerOpenConnection(long serverOpenConnection) {
		serversOpenConections += serverOpenConnection;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
