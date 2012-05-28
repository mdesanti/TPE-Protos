package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import ar.edu.it.itba.pdc.v2.implementations.RebuiltHeader;
import ar.edu.it.itba.pdc.v2.implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.v2.interfaces.Analyzer;
import ar.edu.it.itba.pdc.v2.interfaces.Configurator;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;
import ar.edu.it.itba.pdc.v2.interfaces.HTTPHeaders;

public class AnalyzerImp implements Analyzer {

	private ConnectionManager connectionManager;
	private static int BUFFSIZE = 5 * 1024;
	private Configurator configurator;

	public AnalyzerImp(ConnectionManager connectionManager,
			Configurator configurator) {
		this.connectionManager = connectionManager;
		this.configurator = configurator;
	}

	public void analyze(ByteBuffer buffer, int count, Socket socket) {

		Decoder decoder = new DecoderImpl(BUFFSIZE);
		ByteBuffer resp = ByteBuffer.allocate(BUFFSIZE);
		int receivedMsg = 0, totalCount = 0;
		byte[] buf = new byte[BUFFSIZE];
		boolean keepReading = true;
		HTTPHeaders requestHeaders, responseHeaders;

		// Parse request headers
		decoder.parseHeaders(buffer.array(), count);
		requestHeaders = decoder.getHeaders();

		// Rebuilt the headers according to proxy rules and implementations
		RebuiltHeader rh = decoder.rebuildHeaders();

		Socket externalServer;
		String host = decoder.getHeader("Host").replace(" ", "");
		while ((externalServer = connectionManager.getConnection(host)) == null)
			;
		InputStream externalIs, clientIs;
		OutputStream externalOs, clientOs;
		try {
			externalIs = externalServer.getInputStream();
			externalOs = externalServer.getOutputStream();
			clientIs = socket.getInputStream();
			clientOs = socket.getOutputStream();

			// Sends rebuilt header to server
			externalOs.write(rh.getHeader(), 0, rh.getSize());

			// If client sends something in the body..
			if (requestHeaders.getReadBytes() < count) {
				byte[] extra = decoder.getExtra(buffer.array(), count);
				externalOs.write(extra, 0,
						count - requestHeaders.getReadBytes());
				decoder.analize(extra, count - requestHeaders.getReadBytes());
			} else {
				decoder.analize(buffer.array(), count);
			}
			keepReading = decoder.keepReading();
			// if client continues to send info, read it and send it to server
			while (decoder.keepReading()
					&& ((receivedMsg = clientIs.read(buf)) != -1)) {
				decoder.decode(buf, receivedMsg);
				externalOs.write(buf, 0, receivedMsg);
			}

			// Reads response from server and write it to client
			decoder.reset();
			keepReading = true;
			totalCount = 0;
			try {
				// Read headers
				while (keepReading
						&& ((receivedMsg = externalIs.read(buf)) != -1)) {
					totalCount += receivedMsg;
					resp.put(buf, 0, receivedMsg);
					keepReading = !decoder.completeHeaders(resp.array(),
							resp.array().length);
				}
				// Parse response heaaders
				decoder.parseHeaders(resp.array(), totalCount);
				responseHeaders = decoder.getHeaders();

				// Sends only headers to client
				clientOs.write(resp.array(), 0, responseHeaders.getReadBytes());

				// Sends the rest of the body to client...

				decoder.setConfigurator(configurator);
				boolean applyTransform = decoder.applyTransformations();
				boolean data = false;
				if (responseHeaders.getReadBytes() < totalCount) {
					byte[] extra = decoder.getExtra(resp.array(), totalCount);
					decoder.analize(extra,
							totalCount - responseHeaders.getReadBytes());
					decoder.applyRestrictions(extra, totalCount
							- responseHeaders.getReadBytes(), requestHeaders);
					if (!applyTransform) {
						clientOs.write(extra, 0,
								totalCount - responseHeaders.getReadBytes());
					}
					data = true;
				}
				resp.clear();
				keepReading = decoder.keepReading();
				while (keepReading
						&& ((receivedMsg = externalIs.read(buf)) != -1)) {
					totalCount += receivedMsg;
					decoder.analize(buf, receivedMsg);
					decoder.applyRestrictions(buf, receivedMsg, requestHeaders);
					if (!applyTransform) {
						clientOs.write(buf, 0, receivedMsg);
					}
					keepReading = decoder.keepReading();
					data = true;
				}
				if (applyTransform && data) {
					if (configurator.applyRotations() && decoder.isImage()) {
						byte[] rotated = decoder.getRotatedImage();
						clientOs.write(rotated, 0, rotated.length);
					}
					if (configurator.applyTextTransformation()
							&& decoder.isText()) {
						byte[] transformed = decoder.getTransformed();
						clientOs.write(transformed, 0, transformed.length);
					}
				}

				connectionManager.releaseConnection(externalServer);
				// System.out.println("TERMINO");
			} catch (IOException e) {
				connectionManager.releaseConnection(externalServer);
				System.out.println(e.getMessage());
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (BufferOverflowException e) {
			e.printStackTrace();
		}

	}
}
