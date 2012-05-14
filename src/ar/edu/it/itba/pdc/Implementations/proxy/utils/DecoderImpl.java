package ar.edu.it.itba.pdc.Implementations.proxy.utils;

import java.io.FileOutputStream;
import java.io.IOException;

import ar.edu.it.itba.pdc.Interfaces.Decoder;
import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;

public class DecoderImpl implements Decoder {

	private boolean read = false;
	private int index = 0;
	private HTTPHeaders headers = null;
	private String fileName = null;

	public DecoderImpl(int buffSize) {
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
			int expectedRead = Integer.parseInt(length);
			if (expectedRead > headers.getReadBytes()) {
				if (!headers.contentExpected())
					read = false;
				read = true;
			} else {
				read = false;
			}
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

	@Override
	public void applyRestrictions(byte[] bytes, int count) {

		String contentType = headers.getHeader("Content-Type");

		if (contentType == null)
			return;

		if (contentType.contains("image/")) {
			String extension = contentType.split("/")[1];
			if (fileName == null)
				fileName = "/tmp/prueba" + this.hashCode() + "." + extension;
			try {
				FileOutputStream fw = new FileOutputStream(fileName, true);
				String data = headers.getBody(bytes, count);
				fw.write(data.getBytes());
				fw.close();
				if(!keepReading())
					fileName = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
