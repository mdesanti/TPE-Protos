package ar.edu.it.itba.pdc.v2.interfaces;

import java.net.Socket;
import java.nio.ByteBuffer;

public interface Analyzer {

	public void analyze(ByteBuffer buffer, int count, Socket socket);
	
	public boolean keepConnection();

}
