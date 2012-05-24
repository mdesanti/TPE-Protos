package ar.edu.it.itba.pdc.v2.interfaces;

import java.io.InputStream;
import java.nio.ByteBuffer;

public interface Analyzer {

	public InputStream analyze(ByteBuffer buffer, int count, InputStream clientInputSteam);
	
}
