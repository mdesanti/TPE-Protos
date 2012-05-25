package ar.edu.it.itba.pdc.v2.implementations.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import ar.edu.it.itba.pdc.v2.implementations.utils.DecoderImpl;
import ar.edu.it.itba.pdc.v2.interfaces.Analyzer;
import ar.edu.it.itba.pdc.v2.interfaces.ConnectionManager;
import ar.edu.it.itba.pdc.v2.interfaces.Decoder;

public class AnalyzerImp implements Analyzer {
	
	private ConnectionManager connectionManager;
	private static int BUFFSIZE = 5*1024;
	
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
		decoder.decode(buffer.array(), count);
		
		Socket externalServer;
		String host = decoder.getHeader("Host");
		while((externalServer = connectionManager.getConnection(host)) ==  null);
		InputStream externalIs;
		OutputStream externalOs, clientOs;
		try {
			externalIs = externalServer.getInputStream();
			externalOs = externalServer.getOutputStream();
			clientOs = socket.getOutputStream();
			externalOs.write(buffer.array(), 0, count);
			
			//if client continues to send info, read it and send it to server
			
			//read response from server and write it to client
			try {
				//read headers
				//parse headers and decide what to do
				
				//send whats left of the response to client
				while (((receivedMsg = externalIs.read(buf)) != -1)) {
					totalCount += receivedMsg;
					clientOs.write(buf, 0, receivedMsg);
				}
				connectionManager.releaseConnection(externalServer);
				externalServer.close();
			} catch (IOException e) {
				externalIs.close();
				connectionManager.releaseConnection(externalServer);
				System.out.println(e.getMessage());
			}

		} catch (IOException e) {
		}
		
		
	}
	
}
