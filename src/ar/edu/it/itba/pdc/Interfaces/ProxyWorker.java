package ar.edu.it.itba.pdc.Interfaces;

import java.nio.channels.SocketChannel;

import ar.edu.it.itba.pdc.Implementations.TCPSelector;

public interface ProxyWorker extends Runnable {

	
	public void setServer(TCPSelector serverSelector);
	
	public void setClient(TCPSelector client);

	void sendData(TCPSelector sender, SocketChannel from, byte[] data,
			long count, boolean multiPart);
	
	
}
