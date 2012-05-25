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
	
	public AnalyzerImp(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
	
	@Override
	public InputStream analyze(ByteBuffer buffer, int count, InputStream clientInputSteam) {
		
		Decoder decoder = new DecoderImpl(10*1024);
		int receivedMsg;
		byte[] buf = new byte[10*1024];
		decoder.decode(buffer.array(), count);
		
		Socket externalServer = connectionManager.getConnection(decoder.getHeader("Host"));
		InputStream externalIs;
		OutputStream externalOs;
		try {
			externalIs = externalServer.getInputStream();
			externalOs = externalServer.getOutputStream();
			externalOs.write(buffer.array(), 0, count);
			return externalIs;
		} catch (IOException e) {
			return null;
		}
		
		
	}

}
