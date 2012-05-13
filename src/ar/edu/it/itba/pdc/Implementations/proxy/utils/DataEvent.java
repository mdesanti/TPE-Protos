package ar.edu.it.itba.pdc.Implementations.proxy.utils;

import java.net.URL;
import java.nio.channels.SocketChannel;

import ar.edu.it.itba.pdc.Implementations.proxy.TCPSelector;

public class DataEvent {

	private TCPSelector sender;
	private SocketChannel from;
	private byte[] data;
	private boolean multiPart;
	private URL url;
	
	public DataEvent(TCPSelector sender, SocketChannel from, byte[] data, boolean multiPart, URL url) {
		this.sender = sender;
		this.from = from;
		this.data = data;
		this.multiPart = multiPart;
		this.url = url;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public SocketChannel getFrom() {
		return from;
	}
	
	public TCPSelector getSender() {
		return sender;
	}
	
	public boolean isMulipart() {
		return multiPart;
	}
	
	public URL getUrl() {
		return url;
	}
	
	
}
