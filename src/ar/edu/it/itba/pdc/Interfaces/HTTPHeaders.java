package ar.edu.it.itba.pdc.Interfaces;

public interface HTTPHeaders {

	public String getRequestHeader(String header);
	
	public String getResponseHeader(String header);
	
	public boolean isResponse();
	
	public boolean isRequest();
	
	public int getHeaderSize();
	
	public void parse(byte[] data);
	
}