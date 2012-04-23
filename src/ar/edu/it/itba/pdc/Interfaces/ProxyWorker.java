package ar.edu.it.itba.pdc.Interfaces;

import java.nio.channels.SocketChannel;

import ar.edu.it.itba.pdc.Implementations.TCPSelector;
import ar.edu.it.itba.pdc.Implementations.server.TCPServerSelector;

public interface ProxyWorker extends Runnable {

	public void sendData(TCPSelector sender, SocketChannel from, byte[] data, long count);
	
	public void setServer(TCPSelector serverSelector);
	
	public void setClient(TCPSelector client);
	
	
}
