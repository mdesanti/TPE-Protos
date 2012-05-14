package ar.edu.it.itba.pdc.Interfaces;

import java.nio.channels.SocketChannel;

public interface Attachment {

	public boolean isMultipart();
	
	public SocketChannel getFrom();
}
