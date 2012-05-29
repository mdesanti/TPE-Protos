package ar.edu.it.itba.pdc.v2.implementations.monitor;

import ar.edu.it.itba.pdc.v2.interfaces.DataStorage;

public class DataStorageImpl implements DataStorage {

	private long totalBytes;
	private long clientToProxyBytes;
	private long proxyToServersBytes;
	private long blocks;
	private long transformations;
	private long clientOpenConections;
	private long serversOpenConections;
	private static DataStorageImpl INSTANCE = new DataStorageImpl();

	private DataStorageImpl() {
		this.totalBytes = 0;
		this.clientToProxyBytes = 0;
		this.proxyToServersBytes = 0;
		this.blocks = 0;
		this.transformations = 0;
		this.clientOpenConections = 0;
		this.serversOpenConections = 0;
	}

	public long getTotalBytes() {
		return totalBytes;
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

	public long getClientOpenConections() {
		return clientOpenConections;
	}

	public long getServersOpenConections() {
		return serversOpenConections;
	}

	public static DataStorageImpl getInstance() {
		return INSTANCE;
	}

	public synchronized void addTotalBytes(long bytes) {

		DataStorageImpl.getInstance().totalBytes += bytes;
	}

	public synchronized void addClientProxyBytes(long bytes) {
		DataStorageImpl.getInstance().clientToProxyBytes += bytes;
	}

	public synchronized void addProxyServerBytes(long bytes) {
		DataStorageImpl.getInstance().proxyToServersBytes += bytes;
	}

	public synchronized void addBlock() {
		this.blocks += blocks;
	}

	public synchronized void addTransformation() {
		this.transformations++;
	}

	public synchronized void addClientOpenConeccion(long clientOpenConections) {
		DataStorageImpl.getInstance().clientOpenConections += clientOpenConections;
	}

	public synchronized void addServerOpenConection(long serverOpenConection) {
		DataStorageImpl.getInstance().serversOpenConections += serverOpenConection;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
