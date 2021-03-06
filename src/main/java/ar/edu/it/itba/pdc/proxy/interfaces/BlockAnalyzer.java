package ar.edu.it.itba.pdc.proxy.interfaces;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;


public interface BlockAnalyzer {

	/**
	 * Analyzes if something in the request must be blocked. Like URIS, IPS or
	 * ALL.
	 * 
	 * @param decoder
	 * @param clientOs
	 * @return True if something is blocked. False if nothing is blocked.
	 * @throws IOException
	 */
	public boolean analyzeRequest(Decoder decoder, OutputStream clientOs, Browser b, OperatingSystem os, InetAddress ip)
			throws IOException;

	/**
	 * Analyzes if something in the request must be blocked. Like Media Types or
	 * file size.
	 * 
	 * @param decoder
	 * @param clientOs
	 * @return
	 * @throws IOException
	 */
	public boolean analyzeResponse(Decoder decoder, OutputStream clientOs, Browser b, OperatingSystem os, InetAddress ip)
			throws IOException;

	/**
	 * Analyze if a chunked transfer must be blocked cause to it's size.
	 * 
	 * @param decoder
	 * @param clientOs
	 * @param totalSize
	 * @return
	 * @throws IOException
	 */
	public boolean analyzeChunkedSize(Decoder decoder, OutputStream clientOs,
			int totalSize, Browser b, OperatingSystem os, InetAddress ip) throws IOException;

}
