package ar.edu.it.itba.pdc.Implementations.utils;

import java.util.HashMap;
import java.util.Map;

import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;

/**
 * This class parses an array of bytes and builds up the headers according to de
 * HTTP rfc
 * */
public class HTTPPacket implements HTTPHeaders {

	private Map<String, String> headers;
	private int bodyBytes = 0;
	private boolean completeHeaders = false;

	public HTTPPacket() {

		headers = new HashMap<String, String>();

	}

	public void parse(byte[] data, int count) {
		if (completeHeaders) {
			bodyBytes += count;
			return;
		}
		
		if(count == -1) {
			System.out.println("-1");
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


		String[] lines = message;

		parseHeaders(lines);

		String firstLine = lines[0];
		String[] args = firstLine.split(" ");
		String method = args[0];
		headers.put("Method", method);
		String requestURI = args[1];
		headers.put("RequestedURI", requestURI);
		String httpVersion = args[2];
		headers.put("HTTPVersion", httpVersion);
	}

	private void parseResponse(String[] message) {


		// HTTP-Version SP Status-Code SP Reason-Phrase CRLF
		String[] lines = message;

		parseHeaders(lines);

		String firstLine = lines[0];
		String[] args = firstLine.split(" ");
		String statusCode = args[0];
		headers.put("StatusCode", statusCode);
		String reason = args[1];
		headers.put("Reason", reason);
		String httpVersion = args[0];
		headers.put("HTTPVersion", httpVersion);

	}

	private void parseHeaders(String[] lines) {
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
	public String getHeader(String header) {
		return this.headers.get(header);
	}

	@Override
	public int getReadBytes() {
		return bodyBytes;
	}
}
