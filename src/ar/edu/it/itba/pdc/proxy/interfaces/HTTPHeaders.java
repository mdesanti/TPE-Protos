package ar.edu.it.itba.pdc.proxy.interfaces;

import java.util.Map;

public interface HTTPHeaders {

	public String getHeader(String header);
	
	public int getReadBytes();
	
	public boolean contentExpected();
	
	public String dumpHeaders();
	
	public boolean parseHeaders(byte[] data, int count,String action);
	
	public Map<String, String> getAllHeaders();
	
	public void addHeader(String name, String value);
	
	public boolean isHEADRequest();
}