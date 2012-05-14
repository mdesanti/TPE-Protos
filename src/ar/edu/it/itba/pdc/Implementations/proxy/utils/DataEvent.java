package ar.edu.it.itba.pdc.Implementations.proxy.utils;

import java.nio.channels.SocketChannel;

import ar.edu.it.itba.pdc.Implementations.proxy.TCPSelector;

public class DataEvent {

	private TCPSelector sender;
	private SocketChannel from;
	private byte[] data;
	private boolean multiPart;
	
	public DataEvent(TCPSelector sender, SocketChannel from, byte[] data, boolean multiPart) {
		this.sender = sender;
		this.from = from;
		this.data = data;
		this.multiPart = multiPart;
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
	
	
}
