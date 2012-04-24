package ar.edu.it.itba.pdc.Interfaces;

public interface HTTPHeaders {

	public String getHeader(String header);
	
	public boolean isResponse();
	
	public int getHeaderSize();
	
}