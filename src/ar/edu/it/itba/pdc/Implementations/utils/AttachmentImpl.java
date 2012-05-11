package ar.edu.it.itba.pdc.Implementations.utils;

import java.nio.channels.SocketChannel;

import ar.edu.it.itba.pdc.Interfaces.Attachment;

public class AttachmentImpl implements Attachment {
	
	private boolean multipart = false;
	private SocketChannel from;
	
	public AttachmentImpl(boolean isMultipart, SocketChannel from) {
		this.from = from;
		this.multipart = isMultipart;
	}
	
	@Override
	public SocketChannel getFrom() {
		return from;
	}
	
	@Override
	public boolean isMultipart() {
		return multipart;
	}

}
