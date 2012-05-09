package ar.edu.it.itba.pdc.Implementations.utils;

import java.util.HashMap;
import java.util.Map;

import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;

/**
 * This class parses an array of bytes and builds up the headers according to de
 * HTTP rfc
 * */
public class HTTPPacket implements HTTPHeaders {

	private Map<String, String> requestHeaders;
	private Map<String, String> responseHeaders;
	private boolean isResponse = false;
	private int bodyBytes = 0;
	private boolean completeHeaders = false;

	public HTTPPacket() {

		requestHeaders = new HashMap<String, String>();
		responseHeaders = new HashMap<String, String>();

	}

	public void parse(byte[] data, int count) {
		if (completeHeaders) {
			return;
		}

		String s = new String(data).substring(0, count);
		String aux = new String("\r\n");
		int w = aux.length();

		String[] lines = s.split("\r\n");

		if (lines.length == 0) {
			System.out.println("No deberia pasar");
		}
		String startLine = lines[0];
		String[] args = startLine.split(" ");
		if (args[0].equals("GET") || args[0].equals("POST")
				|| args[0].equals("HEAD")) {
			parseRequest(lines);
		} else if (args[0].contains("HTTP")) {
			parseResponse(lines);
		} else {
			// TODO: not supported
		}

	}

	private void parseRequest(String[] message) {

		isResponse = false;

		String[] lines = message;

		parseHeaders(lines, requestHeaders);

		String firstLine = lines[0];
		String[] args = firstLine.split(" ");
		String method = args[0];
		requestHeaders.put("Method", method);
		String requestURI = args[1];
		requestHeaders.put("RequestedURI", requestURI);
		String httpVersion = args[2];
		requestHeaders.put("HTTPVersion", httpVersion);
	}

	private void parseResponse(String[] message) {

		isResponse = true;

		// HTTP-Version SP Status-Code SP Reason-Phrase CRLF
		String[] lines = message;

		parseHeaders(lines, responseHeaders);

		String firstLine = lines[0];
		String[] args = firstLine.split(" ");
		String statusCode = args[0];
		responseHeaders.put("StatusCode", statusCode);
		String reason = args[1];
		responseHeaders.put("Reason", reason);
		String httpVersion = args[0];
		responseHeaders.put("HTTPVersion", httpVersion);

	}

	private void parseHeaders(String[] lines, Map<String, String> headers) {
		// will read until an empty line appears
		int length = lines.length;
		int i = 1;
		for (i = 1; i < length && !completeHeaders; i++) {
			if (lines[i].isEmpty()) {
				completeHeaders = true;
			} else {
				String[] headerValue = lines[i].split(":");
				headerValue[1] = headerValue[1].replaceAll(" ", "");
				if (headerValue.length < 2) {
					return;
				}
				headers.put(headerValue[0], headerValue[1]);
			}
		}
		//add "\r\n" bytes deleted when splitting
		bodyBytes += (length - i)*2;
		for (; i < length; i++) {
			String buf = "";
			buf += lines[i];
			bodyBytes += buf.getBytes().length;
		}
		
	}

	@Override
	public String getRequestHeader(String header) {
		return this.requestHeaders.get(header);
	}

	@Override
	public String getResponseHeader(String header) {
		return this.responseHeaders.get(header);
	}

	@Override
	public boolean isResponse() {
		return isResponse;
	}

	@Override
	public boolean isRequest() {
		return !isResponse;
	}

	@Override
	public int getHeaderSize() {
		return bodyBytes;
	}
}
