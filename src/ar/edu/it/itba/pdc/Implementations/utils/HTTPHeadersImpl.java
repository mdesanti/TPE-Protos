package ar.edu.it.itba.pdc.Implementations.utils;

import java.util.HashMap;
import java.util.Map;

import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;

/**
 * This class parses an array of bytes and builds up the headers according to de
 * HTTP rfc
 * */
public class HTTPHeadersImpl implements HTTPHeaders {

	private Map<String, String> headers;
	private boolean isResponse = false;
	private int headerBytes = 0;
	private boolean completeHeaders = false;

	public HTTPHeadersImpl(byte[] data) {

		if (completeHeaders)
			return;
		headers = new HashMap<String, String>();

		String s = new String(data);

		String[] lines = s.split("\r\n");

		if (lines.length == 0) {
			System.out.println("No deberia pasar");
		}
		String startLine = lines[0];
		String[] args = startLine.split(" ");
		if (args[0].equals("GET") || args[0].equals("POST")
				|| args[0].equals("HEAD")) {
			parseRequest(s);
		} else if (args[0].contains("HTTP")) {
			parseResponse(s);
		} else {
			// TODO: not supported
		}
	}

	@Override
	public String getHeader(String header) {
		return this.headers.get(header);
	}

	@Override
	public boolean isResponse() {
		return isResponse;
	}

	@Override
	public int getHeaderSize() {
		return headerBytes;
	}

	private void parseRequest(String message) {
		String[] lines = message.split("\r\n");

		parseHeaders(lines);

		String firstLine = lines[0];
		String[] args = firstLine.split(" ");
		String method = args[0];
		headers.put("Method", method);
		String requestURI = args[1];
		headers.put("RequestedURI", requestURI);
		String httpVersion = args[2];
		headers.put("HTTPVersion", httpVersion);
		headerBytes += firstLine.getBytes().length;
	}

	private void parseResponse(String message) {
		// HTTP-Version SP Status-Code SP Reason-Phrase CRLF
		String[] lines = message.split("\r\n");

		parseHeaders(lines);

		String firstLine = lines[0];
		String[] args = firstLine.split(" ");
		String statusCode = args[0];
		headers.put("StatusCode", statusCode);
		String reason = args[1];
		headers.put("Reason", reason);
		String httpVersion = args[0];
		headers.put("HTTPVersion", httpVersion);
		headerBytes += firstLine.getBytes().length;

	}

	private void parseHeaders(String[] lines) {
		// will read until an empty line appears
		boolean emptyLine = false;
		int length = lines.length;
		for (int i = 1; i < length && !emptyLine; i++) {
			headerBytes += lines[i].getBytes().length;
			if (lines[i].isEmpty()) {
				emptyLine = true;
			} else {
				String[] headerValue = lines[i].split(":");
				headerValue[1] = headerValue[1].replaceAll(" ", "");
				if (headerValue.length != 2) {
					return;
				}
				headers.put(headerValue[0], headerValue[1]);
			}
		}
	}

}
