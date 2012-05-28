package ar.edu.it.itba.pdc.v2.implementations.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

import ar.edu.it.itba.pdc.v2.implementations.RebuiltHeader;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;
import ar.edu.it.itba.pdc.v2.interfaces.HTTPHeaders;

public class DecoderImpl implements Decoder {

	private boolean read = true;
	private int index = 0;
	private HTTPHeaders headers = null;
	private String fileName;
	private int keepReadingBytes = 0;
	private boolean rotateImages = false;
	private boolean transformL33t = false;
	private boolean generatingKeep = false;
	private String keepReadingBytesHexa;

	public DecoderImpl(int buffSize) {
		headers = new HTTPPacket();
	}

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

	public byte[] getExtra(byte[] data, int count) {
		String read = null;
		read = new String(data).substring(0, count);
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

	
	public boolean keepReading() {
		return read;
	}

	private boolean isChunked() {
		return (headers.getHeader("Transfer-Encoding") != null)
				&& (headers.getHeader("Transfer-Encoding").contains("chunked"));
	}

	
	public int getBufferSize() {
		return index;
	}

	
	public String getHeader(String header) {
		return headers.getHeader(header);
	}

	
	public void analizeRestrictions() {
		if (headers.getHeader("Content-Type") != null) {
			rotateImages = headers.getHeader("Content-Type").contains("image/");
			transformL33t = headers.getHeader("Content-Type").contains(
					"text/plain");
		}
	}

	public boolean getRotateImages() {
		return rotateImages;
	}

	public boolean getTransformL33t() {
		return transformL33t;
	}

	
	public void applyRestrictions(byte[] bytes, int count,
			HTTPHeaders requestHeaders) {

		// String contentType = headers.getHeader("Content-Type");

		// if (contentType == null)
		// return false;

		if (rotateImages) {
			if (fileName == null) {
				String path[] = requestHeaders.getHeader("RequestedURI").split(
						"/");
				File f = new File("/tmp/prueba");
				f.mkdir();
				if (path[path.length - 1].length() < 10)
					fileName = "/tmp/prueba/" + path[path.length - 1];
				else {

					fileName = "/tmp/prueba/"
							+ path[path.length - 1].substring(0, 6)
							+ "."
							+ headers.getHeader("Content-Type").split("/")[1]
									.split(";")[0];
				}
			}
			try {
				FileOutputStream fw = new FileOutputStream(fileName, true);
				fw.write(bytes, 0, count);
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (transformL33t) {
			if (fileName == null) {
				String path[] = requestHeaders.getHeader("RequestedURI").split(
						"/");
				File f = new File("/tmp/prueba");
				f.mkdir();
				if (path[path.length - 1].length() < 10)
					fileName = "/tmp/prueba/" + path[path.length - 1] + ".txt";
				else {
					fileName = "/tmp/prueba/"
							+ path[path.length - 1].substring(0, 6) + "."
							+ "txt";
				}
			}
			try {
				FileOutputStream fw = new FileOutputStream(fileName, true);
				fw.write(bytes, 0, count);
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	
	public byte[] getRotatedImage() {
		Transformations im = new Transformations();
		// InputStream is = null;
		// try {
		// is = new BufferedInputStream(new FileInputStream((fileName)));
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		byte[] modified = im.rotate(fileName, 180);
		// try {
		// is.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		fileName = null;
		return modified;
	}

	
	public byte[] getTransformed() {
		Transformations im = new Transformations();
		InputStream is = null;
		try {
			System.out.println(fileName);
			is = new BufferedInputStream(new FileInputStream((fileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] modified = null;
		try {
			modified = im.transformL33t(is);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileName = null;
		return modified;
	}

	
	public void applyTransformations(byte[] bytes, int count) {
		// TODO Auto-generated method stub

	}

	
	public void applyFilters() {
		// TODO Auto-generated method stub

	}

	
	public boolean completeHeaders(byte[] bytes, int count) {
		String read = null;
		read = new String(bytes).substring(0, count);
		String[] lines = read.split("\r\n");

		for (String line : lines) {
			if (line.equals("")) {
				return true;
			}
		}

		return false;
	}

	
	public synchronized void analize(byte[] bytes, int count) {
		if (!headers.contentExpected()) {
			keepReadingBytes = 0;
			return;
		}
		if (isChunked()) {
			String[] chunks = null;
			chunks = new String(bytes).substring(0, count).split("\r\n");
			for (int j = 0; j < chunks.length; j++) {
				if (keepReadingBytes == 0) {
					Integer sizeLine = null;
					try {
						sizeLine = Integer.parseInt(chunks[j], 16);
						keepReadingBytesHexa = chunks[j];
					} catch (NumberFormatException e) {
						sizeLine = 0;
					}
					if (sizeLine == 0) {
						read = false;
					}
					keepReadingBytes = sizeLine;
					generatingKeep = true;
				} else {
					if (generatingKeep && j == 0) {
						try {
							Integer.parseInt(chunks[0], 16);
							keepReadingBytesHexa += chunks[0];
							keepReadingBytes = Integer.parseInt(
									keepReadingBytesHexa, 16);
							continue;
						} catch (NumberFormatException e) {
						}
					}
					keepReadingBytes -= chunks[j].length();
					if (keepReadingBytes < 0) {
						System.out
								.println("ESTO NO DEBERIA PASAR, keepReadingBytes <0");
					}
					generatingKeep = false;
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

	
	public void reset() {
		read = true;
		index = 0;
		headers = new HTTPPacket();
		fileName = null;
		keepReadingBytes = 0;
		generatingKeep = false;
		keepReadingBytesHexa = "";
		rotateImages = false;
		transformL33t = false;
	}

	
	public void parseHeaders(byte[] data, int count) {
		headers.parseHeaders(data, count);
	}

	
	public HTTPHeaders getHeaders() {
		return headers;
	}

	
	public RebuiltHeader rebuildHeaders() {
		Map<String, String> allHeaders = headers.getAllHeaders();
		String sb = "";

		allHeaders.remove("Accept-Encoding");
		allHeaders.remove("Proxy-Connection");
		allHeaders.put("Accept-Encoding", "identity");
		sb += allHeaders.get("Method") + " ";
		sb += allHeaders.get("RequestedURI") + " ";
		sb += allHeaders.get("HTTPVersion") + "\r\n";

		for (String key : allHeaders.keySet()) {
			if (!key.equals("Method") && !key.equals("RequestedURI")
					&& !key.equals("HTTPVersion"))
				sb += (key + ":" + allHeaders.get(key) + "\r\n");
		}
		// allHeaders.put("Via", " mu0Proxy");
		// sb += "Via:" + allHeaders.get("Via") + "\r\n";
		sb += ("\r\n");
		return new RebuiltHeader(sb.getBytes(), sb.length());
	}

}
