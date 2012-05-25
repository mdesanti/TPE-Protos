package ar.edu.it.itba.pdc.v2.implementations.utils;

import ar.edu.it.itba.pdc.Implementations.proxy.utils.HTTPPacket;
import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;

public class DecoderImpl implements Decoder {

	private boolean read = false;
	private int index = 0;
	private HTTPHeaders headers = null;
	private String fileName;
	private long time;

	public DecoderImpl(int buffSize) {
		time = System.currentTimeMillis();
	}

	@Override
	public void decode(byte[] bytes, int count) {

		if (headers == null) {
			headers = new HTTPPacket();
		}
		headers.parse(bytes, count);

		String length;
		
		if(headers.getHeader("Method").contains("GET")) {
			read = false;
		}
		
		length = headers.getHeader("Content-Length");
		// remove spaces
		if (length != null) {
			length = length.replaceAll(" ", "");
			int expectedRead = Integer.parseInt(length);
			if (expectedRead >= headers.getReadBytes()) {
				if (!headers.contentExpected())
					read = false;
				read = true;
			} else {
				read = false;
			}
		} else {
			String transferEncoding = headers.getHeader("Transfer-Encoding");
			if (transferEncoding != null
					&& transferEncoding.contains("chunked")) {

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

	@Override
	public void applyRestrictions(byte[] bytes, int count) {

		String contentType = headers.getHeader("Content-Type");

		if (contentType == null)
			return;

		if (contentType.contains("image/")) {
			// String extension = contentType.split("/")[1];
			// if (fileName == null)
			// fileName = "/tmp/prueba" + time + "." + extension;
			// try {
			// FileOutputStream fw = new FileOutputStream(fileName, true);
			// // String data = "fruta";
			// String data = headers.getBody(bytes, count);
			// fw.write(data.getBytes());
			// fw.close();
			// if (!keepReading()) {
			// Transformations im = new Transformations();
			// InputStream is = new BufferedInputStream(
			// new FileInputStream((fileName)));
			// byte[] modified = im.rotate(is, 180);
			// is.close();
			// OutputStream os = new BufferedOutputStream(
			// new FileOutputStream("/tmp/rotated.jpeg"));
			// os.write(modified);
			// os.close();
			// fileName = null;
			// }
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		}

	}

	@Override
	public void applyTransformations(byte[] bytes, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyFilters() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean completeHeaders(byte[] bytes, int count) {
		String read = new String(bytes).substring(0, count);
		String[] lines = read.split("\r\n");
		
		for(String line: lines) {
			if(line.equals("")) {
				return true;
			}
		}
		
		return false;
	}

}
