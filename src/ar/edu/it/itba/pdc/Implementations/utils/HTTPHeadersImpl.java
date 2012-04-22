package ar.edu.it.itba.pdc.Implementations.utils;

import java.util.HashMap;
import java.util.Map;

import ar.edu.it.itba.pdc.Interfaces.HTTPHeaders;

public class HTTPHeadersImpl implements HTTPHeaders {

	private Map<String, String> headers;
	private boolean isResponse = false;

	public HTTPHeadersImpl(byte[] data) {

		headers = new HashMap<String, String>();

		String s = new String(data);
		String[] headers = s.split("\r\n");
		for (String header : headers) {
			if (header.contains("GET") || header.contains("HEAD")
					|| header.contains("POST")) {
				this.headers.put("Method", header.split(" ")[0]);
				this.headers.put("Host", header.split(" ")[1]);
			} else if (header.contains("OK") || header.contains("Bad Request") || header.contains("Found")) {
				isResponse = true;
			} else if (header.isEmpty()) {
				break;
			} else {
				String[] keyValue = header.split(":");
				String key = keyValue[0];
				if (!key.equals("Host"))
					this.headers.put(keyValue[0], keyValue[1]);
			}
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

}
