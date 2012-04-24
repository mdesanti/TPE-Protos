package ar.edu.it.itba.pdc.Implementations.utils;

import ar.edu.it.itba.pdc.Interfaces.Decoder;
import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;

public class DecoderImpl implements Decoder {

	private int buffsize;
	private boolean read = false;
	private byte[] buffer;
	private int index = 0;
	private HTTPHeaders headers = null;
	private int readSize = 0;

	public DecoderImpl(int buffSize) {
		this.buffsize = buffSize;
		buffer = new byte[10 * buffSize];
	}

	@Override
	public void decode(byte[] bytes, int count) {

		if (headers == null)
			headers = new HTTPHeadersImpl(bytes);
		String length = headers.getHeader("Content-Length");
		// remove spaces
		if (length != null)
			length = length.replaceAll(" ", "");
		// TODO: chequear que no me pase del largo del buffer
		//first time
		if(!keepReading()) {
			readSize = count-headers.getHeaderSize();
		}
		for (index = 0; index < count; index++) {
			buffer[index] = bytes[index];
		}
		if (length != null && Integer.parseInt(length) > readSize) {
			read = true;
			return;
		}

	}

	@Override
	public boolean keepReading() {
		return read;
	}

	@Override
	public int getBufferSize() {
		return index;
	}

	@Override
	public byte[] getBuffer() {
		byte[] aux = new byte[buffer.length];
		int i = 0;
		for (i = 0; i < buffer.length; i++) {
			aux[i] = buffer[i];
		}
		buffer = new byte[10 * buffsize];
		index = 0;
		return aux;
	}

	@Override
	public void reset() {
		buffer = new byte[10 * buffsize];
		index = 0;
		read = false;
		headers = null;
	}

	@Override
	public String getHeader(String header) {
		return headers.getHeader(header);
	}

	public HTTPHeaders getHeaders() {
		return headers;
	}

}
