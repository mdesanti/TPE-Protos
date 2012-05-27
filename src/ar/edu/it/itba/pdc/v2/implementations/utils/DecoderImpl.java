package ar.edu.it.itba.pdc.v2.implementations.utils;

import java.nio.ByteBuffer;
import java.util.Map;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import ar.edu.it.itba.pdc.v2.implementations.RebuiltHeader;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;
import ar.edu.it.itba.pdc.v2.interfaces.HTTPHeaders;

public class DecoderImpl implements Decoder {

	private boolean read = true;
	private int index = 0;
	private HTTPHeaders headers = null;
	private String fileName;
	private int keepReadingBytes = 0;
	private long time;

	public DecoderImpl(int buffSize) {
		headers = new HTTPPacket();
		time = System.currentTimeMillis();
	}

	@Override
	public void decode(byte[] bytes, int count) {

		// headers.parse(bytes, count);

		String length;

		if (headers.getHeader("Method").contains("GET")) {
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
	public byte[] getExtra(byte[] data, int count) {
		String read = new String(data).substring(0, count);
		String[] lines = read.split("\r\n");
		boolean found = false;
		ByteBuffer buffer = ByteBuffer.allocate(count - headers.getReadBytes());
		for (int i = 0; i < lines.length; i++) {
			if (found) {
				if (i < lines.length - 1)
					lines[i] += "\r\n";
				buffer.put(lines[i].getBytes());
			}
			if (lines[i].equals("")) {
				found = true;
			}

		}

		return buffer.array();
	}

	@Override
	public boolean keepReading() {
		return read;
	}

	private boolean isChunked() {
		return (headers.getHeader("Transfer-Encoding") != null)
				&& (headers.getHeader("Transfer-Encoding").contains("chunked"));
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

		for (String line : lines) {
			if (line.equals("")) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void analize(byte[] bytes, int count) {
		if (!headers.contentExpected()) {
			keepReadingBytes = 0;
			return;
		}
		if (isChunked()) {
			String[] chunks = new String(bytes).substring(0, count).split(
					"\r\n");
			for (int j = 0; j < chunks.length; j++) {
				if (keepReadingBytes == 0) {
					Integer sizeLine = null;
					try {
						sizeLine = Integer.parseInt(chunks[j], 16);
					} catch (NumberFormatException e) {
						sizeLine = 0;
					}
					if (sizeLine == 0) {
						read = false;
					}
					keepReadingBytes = sizeLine;
				} else {
//					System.out.println(chunks[j].length());
					keepReadingBytes -= chunks[j].length();
				}

			}
		} else if (headers.getHeader("Content-Length") != null) {
			if (keepReadingBytes == 0) {
				keepReadingBytes = Integer.parseInt(headers.getHeader(
						"Content-Length").replaceAll(" ", ""));
			}
			keepReadingBytes -= count;
			if (keepReadingBytes == 0)
				read = false;
		} else {
			read = false;
		}
	}

	@Override
	public void reset() {
		read = true;
		index = 0;
		headers = new HTTPPacket();
	}

	@Override
	public void parseHeaders(byte[] data, int count) {
		headers.parseHeaders(data, count);
	}

	@Override
	public HTTPHeaders getHeaders() {
		return headers;
	}

	@Override
	public RebuiltHeader rebuildHeaders() {
		Map<String, String> allHeaders = headers.getAllHeaders();
		String sb = "";

		allHeaders.remove("Content-Encoding");
		// allHeaders.put("Via", " mu0Proxy");
		sb += allHeaders.get("Method") + " ";
		sb += allHeaders.get("RequestedURI") + " ";
		sb += allHeaders.get("HTTPVersion") + "\r\n";

		allHeaders.remove("Method");
		allHeaders.remove("RequestedURI");
		allHeaders.remove("HTTPVersion");
		for (String key : allHeaders.keySet()) {
			sb += (key + ":" + allHeaders.get(key) + "\r\n");
		}
		sb += ("\r\n");

		return new RebuiltHeader(sb.getBytes(), sb.length());
	}

}
