package ar.edu.it.itba.pdc.Implementations.utils;

import java.nio.channels.SocketChannel;

import ar.edu.it.itba.pdc.Implementations.TCPSelector;

public class DataEvent {

	private TCPSelector sender;
	private SocketChannel from;
	private byte[] data;
	private boolean firstPart;
	
	public DataEvent(TCPSelector sender, SocketChannel from, byte[] data) {
		this.sender = sender;
		this.from = from;
		this.data = data;
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
	
	
}
