package ar.edu.it.itba.pdc.v2.interfaces;

public interface DataStorage {

	public long getTotalBytes();

	public void addTotalBytes(long bytes);

	public long getClientProxyBytes();

	public void addClientProxyBytes(long bytes);

	public long getProxyServersBytes();

	public void addProxyServerBytes(long bytes);

	public long getBlocks();

	public void addBlock();

	public long getTransformations();

	public void addTransformation();

	public long getClientOpenConections();

	public void addClientOpenConeccion(long clientOpenConections);

	public long getServersOpenConections();

	public void addServerOpenConection(long serverOpenConection);
}
