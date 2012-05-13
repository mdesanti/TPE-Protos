package ar.edu.it.itba.pdc.Interfaces;

public interface HTTPHeaders {

	public String getHeader(String header);
	
	public int getReadBytes();
	
	public void parse(byte[] data, int count);
	
	public boolean noContentExpected();
	
}