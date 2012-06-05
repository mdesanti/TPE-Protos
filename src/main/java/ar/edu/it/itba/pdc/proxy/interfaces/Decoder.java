package ar.edu.it.itba.pdc.proxy.interfaces;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;

import ar.edu.it.itba.pdc.proxy.implementations.utils.HTML;
import ar.edu.it.itba.pdc.proxy.implementations.utils.RebuiltHeader;

public interface Decoder {

	/**
	 * 
	 * @return True if there is more data to read.
	 */
	public boolean keepReading();

	/**
	 * Returns an http header.
	 * 
	 * @param header
	 * @return
	 */
	public String getHeader(String header);

	/**
	 * Is responsible of making image rotations or transforming to l33t.
	 * 
	 * @param bytes
	 * @param count
	 * @param requestHeader
	 */
	public void applyRestrictions(byte[] bytes, int count,
			HTTPHeaders requestHeader, Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * Returns a rotated image.
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] getRotatedImage() throws IOException;

	/**
	 * Returns a transformed text.
	 * 
	 * @return
	 */
	public byte[] getTransformed();

	/**
	 * Returns true if all the headers have been read.
	 * 
	 * @param bytes
	 * @param count
	 * @return
	 */
	public boolean completeHeaders(byte[] bytes, int count);

	/**
	 * Resets the decoder.
	 */
	public void reset();

	/**
	 * Parse http headers. Returns false if a request method is not implemented
	 * by the proxy. True otherwise.
	 * 
	 * @param data
	 * @param count
	 * @param action
	 *            Request or response.
	 * @return
	 */
	public boolean parseHeaders(byte[] data, int count, String action);

	/**
	 * Returns an HttpHeader.
	 * 
	 * @return
	 */
	public HTTPHeaders getHeaders();

	/**
	 * When reading an http header, extra bytes may be in the buffer. This
	 * method returns those bytes.
	 * 
	 * @param data
	 * @param count
	 * @return
	 */
	public byte[] getExtra(byte[] data, int count);

	/**
	 * Analyze if a response or request is complete or not.
	 * 
	 * @param bytes
	 * @param count
	 */
	public void analyze(byte[] bytes, int count);

	/**
	 * Rebuilds and returns request http headers according to the proxy rules.
	 * 
	 * @return
	 */
	public RebuiltHeader rebuildRequestHeaders();

	/**
	 * Rebuilds and returns response http headers according to the proxy rules.
	 * 
	 * @return
	 */
	public RebuiltHeader rebuildResponseHeaders();

	/**
	 * 
	 * @return True if transformations will be made by the proxy.
	 */
	public boolean applyTransformations(Browser b, OperatingSystem os, InetAddress ip);

	/**
	 * Returns true if the response is an image.
	 * 
	 * @return
	 */
	public boolean isImage();

	/**
	 * Returns true if the response is text/plain.
	 * 
	 * @return
	 */
	public boolean isText();

	/**
	 * Returns a response header cause by a proxy block action.
	 * 
	 * @param cause
	 *            Response status code.
	 * @return
	 */
	public RebuiltHeader generateBlockedHeader(String cause);

	/**
	 * Returns a response html cause by a proxy block action.
	 * 
	 * @param cause
	 *            Response status code.
	 * @return
	 */
	public HTML generateBlockedHTML(String cause);

	/**
	 * When rotating an image the content-length of the response is modified.
	 * This method generates new response headers with that modification.
	 * 
	 * @param contentLength
	 * @return
	 */
	public RebuiltHeader modifiedContentLength(int contentLength);

	/**
	 * Returns true if the request or the response has something in the body.
	 * 
	 * @return
	 */
	public boolean contentExpected();

	/**
	 * Returns to the client a response according to proxy rules. This method
	 * MUST be used for block actions or erros.
	 * 
	 * @param clientOs
	 * @param cause
	 * @throws IOException
	 */
	public void generateProxyResponse(OutputStream clientOs, String cause)
			throws IOException;

}
