package ar.edu.it.itba.pdc.v2.interfaces;

import java.io.IOException;
import java.io.OutputStream;

public interface BlockAnalizer {

	public boolean analizeRequest(Decoder decoder, OutputStream clientOs)
			throws IOException;

	public boolean analizeResponse(Decoder decoder, OutputStream clientOs)
			throws IOException;

	public boolean analizeChunkedSize(Decoder decoder, OutputStream clientOs,
			int totalSize) throws IOException;
}
