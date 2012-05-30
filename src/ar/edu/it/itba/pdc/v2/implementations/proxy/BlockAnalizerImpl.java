package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.ws.rs.core.MediaType;

import ar.edu.it.itba.pdc.v2.implementations.HTML;
import ar.edu.it.itba.pdc.v2.implementations.RebuiltHeader;
import ar.edu.it.itba.pdc.v2.interfaces.BlockAnalizer;
import ar.edu.it.itba.pdc.v2.interfaces.Configurator;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;

public class BlockAnalizerImpl implements BlockAnalizer {

	Decoder decoder;
	Configurator configurator;

	public BlockAnalizerImpl(Configurator configurator) {
		this.configurator = configurator;
	}

	public boolean analizeRequest(Decoder decoder, OutputStream clientOs)
			throws IOException {
		this.decoder = decoder;
		if (analizeBlockAll(clientOs))
			return true;
		if (analizeBlockIP(clientOs))
			return true;
		if (analizeBlockURL(clientOs))
			return true;
		return false;
	}

	public boolean analizeResponse(Decoder decoder, OutputStream clientOs)
			throws IOException {
		this.decoder = decoder;
		if (analizeBlockMediaType(clientOs))
			return true;
		if (analizeBlockSize(clientOs))
			return true;
		return false;
	}

	public boolean analizeChunkedSize(Decoder decoder, OutputStream clientOs,
			int totalSize) throws IOException {
		if (decoder.getHeader("Transfer-Encoding") == null) {
			return false;
		}
		if (totalSize > configurator.getMaxSize()) {
			generateProxyResponse(clientOs, "MAXSIZE");

			return true;
		}
		return false;
	}

	private RebuiltHeader generateProxyResponseHeader(String cause) {
		return decoder.generateBlockedHeader(cause);
	}

	private HTML generateProxyResponseHTML(String cause) {
		return decoder.generateBlockedHTML(cause);
	}

	private boolean analizeBlockAll(OutputStream clientOs) {
		return configurator.blockAll();
	}

	private boolean analizeBlockIP(OutputStream clientOs) throws IOException {
		try {
			URL url = new URL("http://" + decoder
					.getHeader("Host").replace(" ", ""));
			int port = (url.getPort() == -1) ?80:url.getPort();
			if (!configurator.isAccepted(InetAddress.getByName(url.getHost()))) {
				generateProxyResponse(clientOs, "IP");

				return true;
			}
		} catch (UnknownHostException e) {
			System.out.println("UNKNOWN HOST: " + e.getMessage());
		}
		return false;
	}

	private boolean analizeBlockSize(OutputStream clientOs) throws IOException {
		if (decoder.getHeader("Content-Length") == null) {
			return false;
		}
		Integer length = -1;
		try {
			length = Integer.parseInt(decoder.getHeader("Content-Length")
					.replace(" ", ""));
		} catch (NumberFormatException e) {
			System.out.println("Content-Length inv‡lido");
			return true;
		}
		if (configurator.getMaxSize() != -1
				&& length > configurator.getMaxSize()) {
			generateProxyResponse(clientOs, "MAXSIZE");

			return true;
		}
		return false;
	}

	private boolean analizeBlockURL(OutputStream clientOs) throws IOException {
		if (!configurator.isAccepted(decoder.getHeader("RequestedURI").replace(
				" ", ""))) {
			generateProxyResponse(clientOs, "URI");
			return true;
		}
		return false;

	}

	private boolean analizeBlockMediaType(OutputStream clientOs)
			throws IOException {
		if (decoder.getHeader("Content-Type") != null
				&& !configurator.isAccepted(MediaType.valueOf(decoder
						.getHeader("Content-Type").replace(" ", "")))) {
			generateProxyResponse(clientOs, "CONTENT-TYPE");
			return true;
		}
		return false;

	}

	private void generateProxyResponse(OutputStream clientOs, String cause)
			throws IOException {
		RebuiltHeader newHeader = generateProxyResponseHeader(cause);
		HTML html = generateProxyResponseHTML(cause);
		clientOs.write(newHeader.getHeader(), 0, newHeader.getSize());
		clientOs.write(html.getHTML(), 0, html.getSize());
	}
}
