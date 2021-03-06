package ar.edu.it.itba.pdc.proxy.implementations.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import ar.edu.it.itba.pdc.proxy.interfaces.HTTPHeaders;

/**
 * This class parses an array of bytes and builds up the headers according to de
 * HTTP rfc
 * */
public class HTTPPacket implements HTTPHeaders {

	private Map<String, String> headers;
	private int headerBytes = 0;
	private boolean isHEAD = false;
	private boolean contentExpected = true;

	public HTTPPacket() {

		headers = new HashMap<String, String>();

	}

	/**
	 * Asumes all the headers are in the data sent
	 * */
	public boolean parseHeaders(byte[] data, int count, String action) {

		CharBuffer cb = Charset.forName("ISO-8859-1").decode(
				ByteBuffer.wrap(data, 0, count));
		String s = new String(cb.array());
		String headers[] = s.split("\r\n");
		String startLine = headers[0];
		if (action.equals("request")) {
			if (startLine.contains("GET") || startLine.contains("POST")
					|| startLine.contains("HEAD")) {
				parseRequest(headers);
				return true;
			}
			return false;
		} else {
			if (startLine.contains("HTTP")) {
				parseResponse(headers);
				return true;
			}
			return false;
		}
	}

	private void parseRequest(String[] message) {

		String[] lines = message;
		String firstLine = lines[0];
		headerBytes += firstLine.length() + 2;
		String[] args = firstLine.split(" ");
		String method = args[0];
		headers.put("Method", method);
		String requestURI = args[1];
		headers.put("RequestedURI", requestURI);
		String httpVersion = args[2];
		headers.put("HTTPVersion", httpVersion);

		parseHeaders(lines);
		if (args[0].equals("GET") || args[0].equals("HEAD")) {
			contentExpected = false;
			if (args[0].equals("HEAD")) {
				isHEAD = true;
			}
		}

	}

	private void parseResponse(String[] message) {

		// HTTP-Version SP Status-Code SP Reason-Phrase CRLF
		String[] lines = message;

		String firstLine = lines[0];
		headerBytes += firstLine.length() + 2;
		String[] args = firstLine.split(" ");
		String statusCode = args[1];
		headers.put("StatusCode", statusCode);
		String reason = args[2];
		headers.put("Reason", reason);
		String httpVersion = args[0];
		headers.put("HTTPVersion", httpVersion);

		statusCode = statusCode.replaceAll(" ", "");

		if (statusCode.matches("1..") || statusCode.equals("204")
				|| statusCode.equals("304")) {
			contentExpected = false;
		}

		parseHeaders(lines);
	}

	private void parseHeaders(String[] lines) {
		// will read until an empty line appears
		boolean completeHeaders = false;
		int length = lines.length;
		int i = 1;
		for (i = 1; i < length && !completeHeaders; i++) {
			if (lines[i].isEmpty()) {
				completeHeaders = true;
				headerBytes += 2;
			} else {
				headerBytes += lines[i].length() + 2;
				char[] header = lines[i].toCharArray();
				String headerKey = "";
				String value = "";
				boolean first = true;
				for (int j = 0; j < header.length; j++) {
					if (header[j] == ':' && first) {
						first = false;
						continue;
					}
					if (first)
						headerKey += header[j];
					else
						value += header[j];

				}
				headers.put(headerKey, value);
			}
		}
		if (!completeHeaders) {
			completeHeaders = true;
			headerBytes += 2;
		}

	}

	public String getHeader(String header) {
		return this.headers.get(header);
	}

	public int getReadBytes() {
		return headerBytes;
	}

	public boolean contentExpected() {
		return contentExpected;
	}

	public String dumpHeaders() {
		StringBuffer sb = new StringBuffer();
		for (String h : headers.keySet()) {
			sb.append(h + ": ");
			sb.append(headers.get(h));
		}
		return sb.toString();

	}

	public Map<String, String> getAllHeaders() {
		return headers;
	}

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	public boolean isHEADRequest() {
		return isHEAD;
	}

}
