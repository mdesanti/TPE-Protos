package ar.edu.it.itba.pdc.v2.implementations.monitor;

import ar.edu.it.itba.pdc.v2.interfaces.DataStorage;

public class DataStorageImpl implements DataStorage {

	private Integer totalBytes;
	private Integer clientToProxyBytes;
	private Integer proxyToServersBytes;
	private Integer blocks;
	private Integer transformations;
	private Integer clientOpenConections;
	private Integer serversOpenConections;
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
	public int getTotalBytes() {
		return totalBytes;
	}

	@Override
	public int getClientToProxyBytes() {

		return clientToProxyBytes;
	}

	@Override
	public int getProxyToServersBytes() {
		return proxyToServersBytes;
	}

	@Override
	public int getBlocks() {
		return blocks;
	}

	@Override
	public int getTransformations() {
		return transformations;
	}

	@Override
	public int getClientOpenConections() {
		return clientOpenConections;
	}

	@Override
	public int getServersOpenConections() {
		return serversOpenConections;
	}

	public DataStorageImpl getInstance() {
		return INSTANCE;
	}

	@Override
	public synchronized void addTotalBytes(int bytes) {
		this.totalBytes += bytes;
	}

	@Override
	public synchronized void addClientToProxyBytes(int bytes) {
		this.clientToProxyBytes += bytes;
	}

	@Override
	public synchronized void addProxyToServerBytes(int bytes) {
		this.proxyToServersBytes += bytes;
	}

	@Override
	public synchronized void addBlocks(int blocks) {
		this.blocks += blocks;
	}

	@Override
	public synchronized void addTransformations(int transformations) {
		this.transformations += transformations;
	}

	@Override
	public synchronized void addClientOpenConeccion(int clientOpenConections) {
		this.clientOpenConections += clientOpenConections;
	}

	@Override
	public synchronized void addServerOpenConection(int serverOpenConection) {
		this.serversOpenConections += serverOpenConection;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
