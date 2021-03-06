package ar.edu.it.itba.pdc.proxy.implementations.proxy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Map;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;

import ar.edu.it.itba.pdc.proxy.implementations.configurator.interfaces.Configurator;
import ar.edu.it.itba.pdc.proxy.implementations.utils.HTML;
import ar.edu.it.itba.pdc.proxy.implementations.utils.HTTPPacket;
import ar.edu.it.itba.pdc.proxy.implementations.utils.RebuiltHeader;
import ar.edu.it.itba.pdc.proxy.implementations.utils.Transformations;
import ar.edu.it.itba.pdc.proxy.interfaces.Decoder;
import ar.edu.it.itba.pdc.proxy.interfaces.HTTPHeaders;

public class DecoderImpl implements Decoder {

	private boolean read = true;
	private HTTPHeaders headers = null;
	private String fileName;
	private int keepReadingBytes = 0;
	private boolean isImage = false;
	private boolean isText = false;
	private Configurator configurator;
	private byte[] aux;
	private int auxIndex = 0;
	private Charset charset = null;

	private boolean BUILDING_NUMBER = true;
	private boolean N_EXPECTED = false;
	private boolean SECN_EXPECTED = false;
	private boolean R_EXPECTED = false;
	private boolean SECR_EXPECTED = false;
	private boolean READING_CONTENT = false;
	private boolean FINISHED = false;

	public DecoderImpl(Configurator configurator) {
		headers = new HTTPPacket();
		aux = new byte[100];
		this.configurator = configurator;
	}

	public byte[] getExtra(byte[] data, int count) {
		byte[] bytes = new byte[count - headers.getReadBytes()];
		int i = 0;
		boolean R_EXPECTED = true;
		boolean N_EXPECTED = false;
		boolean SECR_EXPECTED = false;
		boolean SECN_EXPECTED = false;
		for (int j = 0; j < count; j++) {
			if (R_EXPECTED && data[j] == '\r') {
				R_EXPECTED = false;
				N_EXPECTED = true;
			} else if (N_EXPECTED && data[j] == '\n') {
				N_EXPECTED = false;
				SECR_EXPECTED = true;
			} else if (N_EXPECTED) {
				N_EXPECTED = false;
				R_EXPECTED = true;
			} else if (SECR_EXPECTED && data[j] == '\r') {
				SECR_EXPECTED = false;
				SECN_EXPECTED = true;
			} else if (SECR_EXPECTED) {
				SECR_EXPECTED = false;
				R_EXPECTED = true;
			} else if (SECN_EXPECTED && data[j] == '\n') {
				j++;
				for (i = 0; i < count - headers.getReadBytes() && j < count; i++) {
					bytes[i] = data[j++];
				}
				return bytes;
			} else {
				SECN_EXPECTED = false;
				R_EXPECTED = true;
			}

		}

		return bytes;
	}

	public boolean keepReading() {
		return read;
	}

	public boolean contentExpected() {
		return headers.contentExpected();
	}

	private boolean isChunked() {
		return (headers.getHeader("Transfer-Encoding") != null)
				&& (headers.getHeader("Transfer-Encoding").contains("chunked"));
	}

	public String getHeader(String header) {
		return headers.getHeader(header);
	}

	private void analizeMediaType() {
		if (headers.getHeader("Content-Type") != null) {
			isImage = headers.getHeader("Content-Type").contains("image/");
			isText = headers.getHeader("Content-Type").contains("text/plain");
		}
	}

	public boolean applyTransformations(Browser b, OperatingSystem os,
			InetAddress ip) {
		this.analizeMediaType();
		return (configurator.applyRotationsFor(b, os, ip) && isImage)
				|| (configurator.applyTextTransformationFor(b, os, ip) && isText);
	}

	public void applyRestrictions(byte[] bytes, int count,
			HTTPHeaders requestHeaders, Browser b, OperatingSystem os,
			InetAddress ip) {

		this.analizeMediaType();

		if (isImage && configurator.applyRotationsFor(b, os, ip)) {
			if (fileName == null) {
				String path[] = requestHeaders.getHeader("RequestedURI").split(
						"/");
				File f = new File("/tmp/proxyFiles");
				f.mkdir();
				if (path[path.length - 1].length() < 10)
					fileName = "/tmp/proxyFiles/"
							+ String.valueOf(System.currentTimeMillis())
							+ Thread.currentThread().getId()
							+ path[path.length - 1];
				else {

					fileName = "/tmp/proxyFiles/"
							+ path[path.length - 1].substring(0, 6)
							+ String.valueOf(System.currentTimeMillis())
							+ Thread.currentThread().getId()
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
			}

		} else if (isText && configurator.applyTextTransformationFor(b, os, ip)) {
			if (fileName == null) {
				String[] params = headers.getHeader("Content-Type").split(";");
				if (params.length < 2) {
					charset = Charset.forName("UTF-8");
				} else {
					String set = params[1].split("=")[1].replace(" ", "");
					charset = Charset.forName(set);
				}

				String path[] = requestHeaders.getHeader("RequestedURI").split(
						"/");
				File f = new File("/tmp/proxyFiles");
				f.mkdir();
				if (path[path.length - 1].length() < 10)
					fileName = "/tmp/proxyFiles/" + path[path.length - 1]
							+ ".txt";
				else {
					fileName = "/tmp/proxyFiles/"
							+ path[path.length - 1].substring(0, 6) + "."
							+ "txt";
				}
			}
			try {
				FileWriter fw = new FileWriter(fileName, true);
				ByteBuffer buf = ByteBuffer.wrap(bytes, 0, count);
				CharBuffer cbuf = charset.decode(buf);
				fw.write(cbuf.array(), 0, cbuf.length());
				fw.close();
			} catch (IOException e) {
			}
		}

	}

	public boolean isImage() {
		return isImage;
	}

	public boolean isText() {
		return isText;
	}

	public byte[] getRotatedImage() throws IOException {
		Transformations im = new Transformations();

		byte[] modified = im.rotate(fileName, 180);
		if (modified == null) {
			return null;
		}
		File f = new File(fileName);
		f.setWritable(true);
		f.delete();
		fileName = null;
		return modified;
	}

	public byte[] getTransformed() {
		Transformations im = new Transformations();
		InputStream is = null;
		try {
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
		File f = new File(fileName);
		f.setWritable(true);
		f.delete();
		fileName = null;
		return modified;
	}

	public boolean completeHeaders(byte[] bytes, int count) {
		boolean R_EXPECTED = true;
		boolean N_EXPECTED = false;
		boolean SECR_EXPECTED = false;
		boolean SECN_EXPECTED = false;
		for (int j = 0; j < count; j++) {
			if (R_EXPECTED && bytes[j] == '\r') {
				R_EXPECTED = false;
				N_EXPECTED = true;
			} else if (N_EXPECTED && bytes[j] == '\n') {
				N_EXPECTED = false;
				SECR_EXPECTED = true;
			} else if (N_EXPECTED) {
				N_EXPECTED = false;
				R_EXPECTED = true;
			} else if (SECR_EXPECTED && bytes[j] == '\r') {
				SECR_EXPECTED = false;
				SECN_EXPECTED = true;
			} else if (SECR_EXPECTED) {
				SECR_EXPECTED = false;
				R_EXPECTED = true;
			} else if (SECN_EXPECTED && bytes[j] == '\n') {
				return true;
			} else {
				SECN_EXPECTED = false;
				R_EXPECTED = true;
			}

		}
		return false;

	}

	public void analyze(byte[] bytes, int count) {

		if (!headers.contentExpected()) {
			keepReadingBytes = 0;
			read = false;
			return;
		}
		if (isChunked()) {
			for (int j = 0; j < count; j++) {
				if (BUILDING_NUMBER && !N_EXPECTED) {
					if (bytes[j] == '\r') {
						N_EXPECTED = true;
					} else {
						aux[auxIndex++] = bytes[j];
					}
				} else if (BUILDING_NUMBER && N_EXPECTED) {
					if (bytes[j] != '\n' && bytes[j] != '0') {
						System.out.println("NO DEBERIA PASAR");
					}
					Integer sizeLine = null;
					try {
						sizeLine = Integer.parseInt(
								new String(aux, 0, auxIndex), 16);
					} catch (NumberFormatException e) {
						sizeLine = 0;
					}
					if (sizeLine == 0) {
						read = false;
						FINISHED = true;
						BUILDING_NUMBER = false;
						N_EXPECTED = false;
						continue;
					}
					keepReadingBytes = sizeLine;
					auxIndex = 0;
					READING_CONTENT = true;
					BUILDING_NUMBER = false;
					N_EXPECTED = false;
					R_EXPECTED = false;
				} else if (READING_CONTENT) {
					keepReadingBytes -= 1;
					if (keepReadingBytes == 0) {
						READING_CONTENT = false;
						BUILDING_NUMBER = false;
						R_EXPECTED = true;
						N_EXPECTED = false;
					}
					if (keepReadingBytes < 0) {
						System.out.println("OUCH");
					}
				} else if (R_EXPECTED && !FINISHED) {
					R_EXPECTED = false;
					N_EXPECTED = true;
				} else if (N_EXPECTED && !FINISHED) {
					N_EXPECTED = false;
					BUILDING_NUMBER = true;
				} else if (FINISHED) {
					if (R_EXPECTED && bytes[j] == '\r') {
						R_EXPECTED = false;
						N_EXPECTED = true;
					} else if (N_EXPECTED) {
						N_EXPECTED = false;
						SECR_EXPECTED = true;
					} else if (SECR_EXPECTED && bytes[j] == '\r') {
						SECR_EXPECTED = false;
						SECN_EXPECTED = true;
					} else if (SECR_EXPECTED) {
						SECR_EXPECTED = false;
						R_EXPECTED = true;
					} else if (SECN_EXPECTED) {
						read = false;
						auxIndex = 0;
					}

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
			read = true;
		}
	}

	public void reset() {
		read = true;
		headers = new HTTPPacket();
		fileName = null;
		keepReadingBytes = 0;
		isImage = false;
		isText = false;
		BUILDING_NUMBER = true;
		N_EXPECTED = false;
		R_EXPECTED = false;
		READING_CONTENT = false;
		SECN_EXPECTED = false;
		SECR_EXPECTED = false;
		FINISHED = false;
		auxIndex = 0;
	}

	public boolean parseHeaders(byte[] data, int count, String action) {
		return headers.parseHeaders(data, count, action);
	}

	public HTTPHeaders getHeaders() {
		return headers;
	}

	public void setConfigurator(Configurator configurator) {
		this.configurator = configurator;

	}

	public RebuiltHeader generateBlockedHeader(String cause) {
		HTTPHeaders newHeaders = new HTTPPacket();
		if (cause.equals("455")) {
			newHeaders.addHeader("StatusCode", "455");
			newHeaders.addHeader("Reason", "Blocked URL");
		} else if (cause.equals("456")) {
			newHeaders.addHeader("StatusCode", "456");
			newHeaders.addHeader("Reason", "Blocked MediaType");
		} else if (cause.equals("453")) {
			newHeaders.addHeader("StatusCode", "453");
			newHeaders.addHeader("Reason", "Blocked IP");
		} else if (cause.equals("451")) {
			newHeaders.addHeader("StatusCode", "451");
			newHeaders.addHeader("Reason", "Blocked File Size");
		} else if (cause.equals("452")) {
			newHeaders.addHeader("StatusCode", "452");
			newHeaders.addHeader("Reason", "All Blocked");
		} else if (cause.equals("500")) {
			newHeaders.addHeader("StatusCode", "500");
			newHeaders.addHeader("Reason", "Internal Server Error");
		} else if (cause.equals("400")) {
			newHeaders.addHeader("StatusCode", "400");
			newHeaders.addHeader("Reason", "Bad Request");
		} else if (cause.equals("501")) {
			newHeaders.addHeader("StatusCode", "501");
			newHeaders.addHeader("Reason", "Not Implemented");
		}
		newHeaders.addHeader("HTTPVersion", "HTTP/1.1");
		newHeaders.addHeader("Via", " mu0");
		newHeaders.addHeader("Content-Type", " text/html; charset=iso-8859-1");
		newHeaders.addHeader("Connection", " close");

		Map<String, String> allHeaders = newHeaders.getAllHeaders();
		StringBuilder sb = new StringBuilder();

		sb.append(allHeaders.get("HTTPVersion")).append(" ");
		sb.append(allHeaders.get("StatusCode")).append(" ");
		sb.append(allHeaders.get("Reason")).append("\r\n");

		for (String key : allHeaders.keySet()) {
			if (!key.equals("HTTPVersion") && !key.equals("StatusCode")
					&& !key.equals("Reason"))
				sb.append(key + ":" + allHeaders.get(key)).append("\r\n");
		}
		sb.append("\r\n");
		return new RebuiltHeader(sb.toString().getBytes(), sb.toString()
				.length());
	}

	public HTML generateBlockedHTML(String cause) {
		StringBuilder html = new StringBuilder();
		if (cause.equals("455")) {
			html.append("<!DOCTYPE HTML PUBLIC ''-//IETF//DTD HTML 2.0//EN'>"
					+ "<html><head>" + "<title>455 URL blocked</title>"
					+ "</head><body>" + "<h1>455 - URL blocked</h1>"
					+ "<p>Your proxy blocked access to this URL.<br />" + "</p>"
					+ "</body></html>");

		} else if (cause.equals("456")) {
			html.append("<h1>Media Type blocked</h1>"
					+ "<p>Your proxy blocked access to this media type.<br />"
					+ "</p>");

		} else if (cause.equals("453")) {
			html.append("<!DOCTYPE HTML PUBLIC ''-//IETF//DTD HTML 2.0//EN'>"
					+ "<html><head>" + "<title>453 IP blocked</title>"
					+ "</head><body>" + "<h1>453 - IP blocked</h1>"
					+ "<p>Your proxy blocked access to this IP.<br />" + "</p>"
					+ "</body></html>");

		} else if (cause.equals("451")) {
			html.append("<!DOCTYPE HTML PUBLIC ''-//IETF//DTD HTML 2.0//EN'>"
					+ "<html><head>"
					+ "<title>451 File size blocked</title>"
					+ "</head><body>" + "<h1>451 - File size blocked</h1>"
					+ "<p>Your proxy blocked files with this certain size.<br />"
					+ "</p>" + "</body></html>");

		} else if (cause.equals("452")) {
			html.append("<!DOCTYPE HTML PUBLIC ''-//IETF//DTD HTML 2.0//EN'>"
					+ "<html><head>" + "<title>452 All Blocked</title>"
					+ "</head><body>" + "<h1>452 - All Blocked</h1>"
					+ "<p>Your proxy blocked all access.<br />" + "</p>"
					+ "</body></html>");

		} else if (cause.equals("500")) {
			html.append("<!DOCTYPE HTML PUBLIC ''-//IETF//DTD HTML 2.0//EN'>"
					+ "<html><head>"
					+ "<title>500 Internal Server Error</title>"
					+ "</head><body>" + "<h1>500 - Internal Server Error</h1>"
					+ "<p>Internal Server Error<br />" + "</p>"
					+ "</body></html>");

		} else if (cause.equals("400")) {
			html.append("<!DOCTYPE HTML PUBLIC ''-//IETF//DTD HTML 2.0//EN'>"
					+ "<html><head>" + "<title>400 Bad Request</title>"
					+ "</head><body>" + "<h1>400 - Bad Request</h1>"
					+ "<p>Bad Request<br />" + "</p>" + "</body></html>");

		} else if (cause.equals("501")) {
			html.append("<!DOCTYPE HTML PUBLIC ''-//IETF//DTD HTML 2.0//EN'>"
					+ "<html><head>" + "<title>501 Not Implemented</title>"
					+ "</head><body>" + "<h1>501 - Method Not Implemented</h1>"
					+ "<p>Your proxy does not implement this method.<br />" + "</p>" + "</body></html>");

		}
		return new HTML(html.toString().getBytes(), html.toString().length());
	}

	public RebuiltHeader modifiedContentLength(int contentLength,
			boolean transformationApplied) {
		Map<String, String> allHeaders = headers.getAllHeaders();
		StringBuilder sb = new StringBuilder();

		allHeaders.remove("Accept-Encoding");
		allHeaders.remove("Proxy-Connection");
		allHeaders.put("Accept-Encoding", "identity");
		allHeaders.remove("Content-Length");
		allHeaders.remove("Transfer-Encoding");
		allHeaders.put("Content-Length", String.valueOf(contentLength));
		sb.append(allHeaders.get("HTTPVersion")).append(" ");
		sb.append(allHeaders.get("StatusCode")).append(" ");
		sb.append(allHeaders.get("Reason")).append("\r\n");

		for (String key : allHeaders.keySet()) {
			if (!key.equals("HTTPVersion") && !key.equals("StatusCode")
					&& !key.equals("Reason"))
				sb.append(key + ":" + allHeaders.get(key)).append("\r\n");
		}
		if (transformationApplied)
			sb.append("Warning: 214 mu0-Proxy\r\n");
		sb.append("Via: mu0-Proxy\r\n");
		sb.append("\r\n");
		return new RebuiltHeader(sb.toString().getBytes(), sb.toString()
				.length());
	}

	public RebuiltHeader rebuildRequestHeaders() {
		Map<String, String> allHeaders = headers.getAllHeaders();
		String reqURI = allHeaders.get("RequestedURI");
		if (!reqURI.startsWith("/"))
			try {
				URL url = new URL(allHeaders.get("RequestedURI"));
				String path = url.getPath();
				if (path.isEmpty()) {
					path += "/";
				}
				if (url.getQuery() != null)
					path += "?" + url.getQuery();
				allHeaders.put("RequestedURI", path);
			} catch (MalformedURLException e) {
			}
		final StringBuilder sb = new StringBuilder();

		String rmP = allHeaders.get("Proxy-Connection");
		if (rmP != null) {
			rmP.replace(" ", "");
			allHeaders.remove(rmP);
		}
		String rmC = allHeaders.get("Connection");
		if (rmC != null) {
			rmC.replace(" ", "");
			allHeaders.remove(rmC);
		}
		allHeaders.remove("Proxy-Connection");
		allHeaders.remove("Connection");
		allHeaders.remove("Accept-Encoding");
		allHeaders.put("Connection", "keep-alive");
		sb.append(allHeaders.get("Method")).append(" ");
		sb.append(allHeaders.get("RequestedURI")).append(" ");
		sb.append(allHeaders.get("HTTPVersion")).append("\r\n");

		for (String key : allHeaders.keySet()) {
			if (!key.equals("Method") && !key.equals("RequestedURI")
					&& !key.equals("HTTPVersion"))
				sb.append(key).append(": ").append(allHeaders.get(key))
						.append("\r\n");
		}
		sb.append("Accept-Encoding: identity\r\n");
		sb.append("Via: HTTP/1.1 mu0-Proxy\r\n");
		sb.append("\r\n");
		return new RebuiltHeader(sb.toString().getBytes(), sb.toString()
				.length());
	}

	public RebuiltHeader rebuildResponseHeaders(boolean keepAlive) {
		Map<String, String> allHeaders = headers.getAllHeaders();
		StringBuilder sb = new StringBuilder();

		String token = allHeaders.get("Connection");
		if (token != null) {
			token.replace(" ", "");
			allHeaders.remove(token);
		}
		allHeaders.remove("Connection");
		allHeaders.remove("HTTPVersion");
		allHeaders.put("HTTPVersion", "HTTP/1.1");
		sb.append(allHeaders.get("HTTPVersion")).append(" ");
		sb.append(allHeaders.get("StatusCode")).append(" ");
		sb.append(allHeaders.get("Reason")).append("\r\n");
		if (keepAlive) {
			sb.append("Connection: Keep-Alive\r\n");
			sb.append("Keep-Alive: timeout=5\r\n");
		}
		else
			sb.append("Connection: close\r\n");

		for (String key : allHeaders.keySet()) {
			if (!key.equals("HTTPVersion") && !key.equals("StatusCode")
					&& !key.equals("Reason"))
				sb.append(key).append(": ")
						.append(allHeaders.get(key) + "\r\n");
		}
		sb.append("Via: HTTP/1.1 mu0-Proxy\r\n");
		sb.append("\r\n");
		return new RebuiltHeader(sb.toString().getBytes(), sb.toString()
				.length());
	}

	public void generateProxyResponse(OutputStream clientOs, String cause)
			throws IOException {
		RebuiltHeader newHeader = generateBlockedHeader(cause);
		HTML html = generateBlockedHTML(cause);
		clientOs.write(newHeader.getHeader(), 0, newHeader.getSize());
		clientOs.write(html.getHTML(), 0, html.getSize());
	}

}
