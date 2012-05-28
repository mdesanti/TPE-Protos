package ar.edu.it.itba.pdc.v2.interfaces;

public interface DataStorage {

	public int getTotalBytes();

	public void addTotalBytes(int bytes);

	public int getClientToProxyBytes();

	public void addClientToProxyBytes(int bytes);

	public int getProxyToServersBytes();

	public void addProxyToServerBytes(int bytes);

	public int getBlocks();

	public void addBlocks(int blocks);

	public int getTransformations();

	public void addTransformations(int transformations);

	public int getClientOpenConections();

	public void addClientOpenConeccion(int clientOpenConections);

	public int getServersOpenConections();

	public void addServerOpenConection(int serverOpenConection);
}
