package ar.edu.it.itba.pdc.Implementations.proxy.utils;

import ar.edu.it.itba.pdc.Interfaces.Decoder;
import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;

public class DecoderImpl implements Decoder {

	private int buffsize;
	private boolean read = false;
	private int index = 0;
	private HTTPHeaders headers = null;
	private int readSize = 0;

	public DecoderImpl(int buffSize) {
		this.buffsize = buffSize;
	}

	@Override
	public void decode(byte[] bytes, int count) {

		if (headers == null) {
			headers = new HTTPPacket();
		}
		headers.parse(bytes, count);

		String length;
		length = headers.getHeader("Content-Length");
		// remove spaces
		if (length != null) {
			length = length.replaceAll(" ", "");
			int aux = Integer.parseInt(length);
			if (aux > headers.getReadBytes() && !headers.noContentExpected()) {
				read = true;
			} else {
				read = false;
			}
		}

	}

	@Override
	public void applyRestrictions() {
		String contentType = headers.getHeader("Content-Type");
		boolean image = false;
		if (contentType != null)
			image = contentType.contains("image/");

		if (image) {
			System.out.println("Es una imagen");
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
	public String getHeader(String header) {
		return headers.getHeader(header);
	}

	public HTTPHeaders getHeaders() {
		return headers;
	}

}
