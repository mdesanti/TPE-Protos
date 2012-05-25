package ar.edu.it.itba.pdc.v2.interfaces;

import java.util.Map;

public interface HTTPHeaders {

	public String getHeader(String header);
	
	public int getReadBytes();
	
	public boolean contentExpected();
	
	public void dumpHeaders();
	
	public void parseHeaders(byte[] data, int count);
	
	public Map<String, String> getAllHeaders();
	
}