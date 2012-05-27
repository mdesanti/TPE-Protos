package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import ar.edu.it.itba.pdc.v2.implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.v2.interfaces.Analyzer;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;
import ar.edu.it.itba.pdc.v2.interfaces.HTTPHeaders;

public class AnalyzerImp implements Analyzer {

	private ConnectionManager connectionManager;
	private static int BUFFSIZE = 5 * 1024;

	public AnalyzerImp(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	@Override
	public void analyze(ByteBuffer buffer, int count, Socket socket) {

		Decoder decoder = new DecoderImpl(BUFFSIZE);
		ByteBuffer resp = ByteBuffer.allocate(BUFFSIZE);
		int receivedMsg, totalCount = 0;
		byte[] buf = new byte[BUFFSIZE];
		boolean keepReading = true;
		HTTPHeaders headers;
		decoder.parseHeaders(buffer.array(), count);
		headers = decoder.getHeaders();


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
			externalOs.write(buffer.array(), 0, headers.getReadBytes());
			
			if (headers.getReadBytes() < count) {
				byte[] extra = decoder.getExtra(buffer.array(), count);
				externalOs.write(extra, 0,
						count - headers.getReadBytes());
				decoder.analize(extra,
						count - headers.getReadBytes());
			} else {
				decoder.analize(buffer.array(), count);
			}
			keepReading = decoder.keepReading();
			// if client continues to send info, read it and send it to server
			while (decoder.keepReading()
					&& ((receivedMsg = clientIs.read(buf)) != -1)) {
				// totalCount += receivedMsg;
				decoder.decode(buf, receivedMsg);
				externalOs.write(buf, 0, receivedMsg);
			}
			// read response from server and write it to client
			try {
				decoder.reset();
				keepReading = true;
				// read headers
				while (keepReading
						&& ((receivedMsg = externalIs.read(buf)) != -1)) {
					totalCount += receivedMsg;
					resp.put(buf, 0, receivedMsg);
					keepReading = !decoder.completeHeaders(resp.array(),
							resp.array().length);
				}
				// parse headers and decide what to do
				decoder.parseHeaders(resp.array(), totalCount);

				headers = decoder.getHeaders();

				// sends only headers to server
				clientOs.write(resp.array(), 0, headers.getReadBytes());

				if (headers.getReadBytes() < totalCount) {
					byte[] extra = decoder.getExtra(resp.array(), totalCount);
					clientOs.write(extra, 0,
							totalCount - headers.getReadBytes());
					decoder.analize(extra,
							totalCount - headers.getReadBytes());
				}
				resp.clear();
				keepReading = decoder.keepReading();
				while (keepReading
						&& ((receivedMsg = externalIs.read(buf)) != -1)) {
					totalCount += receivedMsg;
					clientOs.write(buf, 0, receivedMsg);
					decoder.analize(buf, receivedMsg);
					keepReading = decoder.keepReading();
				}
				connectionManager.releaseConnection(externalServer);
//				System.out.println("TERMINO");
			} catch (IOException e) {
				connectionManager.releaseConnection(externalServer);
				System.out.println(e.getMessage());
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch(BufferOverflowException e) {
			e.printStackTrace();
		}

	}

}
