package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.v2.interfaces.BlockAnalizer;
import ar.edu.it.itba.pdc.v2.interfaces.Configurator;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;

public class BlockAnalizerImpl implements BlockAnalizer {

	private Decoder decoder;
	private Configurator configurator;
	private Logger logger;

	public BlockAnalizerImpl(Configurator configurator,Decoder decoder, Logger logger) {
		this.configurator = configurator;
		this.decoder = decoder;
		this.logger = logger;
	}

	public boolean analyzeRequest(Decoder decoder, OutputStream clientOs)
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

	public boolean analyzeResponse(Decoder decoder, OutputStream clientOs)
			throws IOException {
		this.decoder = decoder;
		if (analizeBlockMediaType(clientOs))
			return true;
		if (analizeBlockSize(clientOs))
			return true;
		return false;
	}

	public boolean analyzeChunkedSize(Decoder decoder, OutputStream clientOs,
			int totalSize) throws IOException {
		if (decoder.getHeader("Transfer-Encoding") == null) {
			return false;
		}
		if(configurator.getMaxSize() == -1)
			return false;
		if (totalSize > configurator.getMaxSize()) {
			logger.info("Block analyzer blocked request with code 451. Returning");
			decoder.generateProxyResponse(clientOs, "451");

			return true;
		}
		return false;
	}

	
	private boolean analizeBlockAll(OutputStream clientOs) throws IOException {
		if (configurator.blockAll()) {
			logger.info("Block analyzer blocked request with code 452. Returning");
			decoder.generateProxyResponse(clientOs, "452");
			return true;
		}
		return false;
	}

	private boolean analizeBlockIP(OutputStream clientOs) throws IOException {
		try {
			if(decoder.getHeader("Host") == null)
				return false;
			URL url = new URL("http://" + decoder
					.getHeader("Host").replace(" ", ""));
			if (!configurator.isAccepted(InetAddress.getByName(url.getHost()))) {
				logger.info("Block analyzer blocked request with code 453. Returning");
				decoder.generateProxyResponse(clientOs, "453");

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
			logger.info("Block analyzer blocked request with code 451. Returning");
			decoder.generateProxyResponse(clientOs, "451");

			return true;
		}
		return false;
	}

	private boolean analizeBlockURL(OutputStream clientOs) throws IOException {
		if (decoder.getHeader("RequestedURI")!=null && !configurator.isAccepted(decoder.getHeader("RequestedURI").replace(
				" ", ""))) {
			logger.info("Block analyzer blocked request with code 455. Returning");
			decoder.generateProxyResponse(clientOs, "455");
			return true;
		}
		return false;

	}

	private boolean analizeBlockMediaType(OutputStream clientOs)
			throws IOException {
		if (decoder.getHeader("Content-Type") != null
				&& !configurator.isAccepted(MediaType.valueOf(decoder
						.getHeader("Content-Type").replace(" ", "")))) {
			logger.info("Block analyzer blocked request with code 456. Returning");
			decoder.generateProxyResponse(clientOs, "456");
			return true;
		}
		return false;

	}

}
