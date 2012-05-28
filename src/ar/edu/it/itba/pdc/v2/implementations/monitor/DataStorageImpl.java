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

	@Override
	public long getTotalBytes() {
		return totalBytes;
	}

	@Override
	public long getClientProxyBytes() {

		return clientToProxyBytes;
	}

	@Override
	public long getProxyServersBytes() {
		return proxyToServersBytes;
	}

	@Override
	public long getBlocks() {
		return blocks;
	}

	@Override
	public long getTransformations() {
		return transformations;
	}

	@Override
	public long getClientOpenConections() {
		return clientOpenConections;
	}

	@Override
	public long getServersOpenConections() {
		return serversOpenConections;
	}

	public static DataStorageImpl getInstance() {
		return INSTANCE;
	}

	@Override
	public synchronized void addTotalBytes(long bytes) {

		DataStorageImpl.getInstance().totalBytes += bytes;
	}

	@Override
	public synchronized void addClientProxyBytes(long bytes) {
		DataStorageImpl.getInstance().clientToProxyBytes += bytes;
	}

	@Override
	public synchronized void addProxyServerBytes(long bytes) {
		DataStorageImpl.getInstance().proxyToServersBytes += bytes;
	}

	@Override
	public synchronized void addBlock() {
		this.blocks += blocks;
	}

	@Override
	public synchronized void addTransformation() {
		this.transformations++;
	}

	@Override
	public synchronized void addClientOpenConeccion(long clientOpenConections) {
		DataStorageImpl.getInstance().clientOpenConections += clientOpenConections;
	}

	@Override
	public synchronized void addServerOpenConection(long serverOpenConection) {
		DataStorageImpl.getInstance().serversOpenConections += serverOpenConection;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
