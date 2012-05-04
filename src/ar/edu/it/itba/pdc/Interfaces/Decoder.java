package ar.edu.it.itba.pdc.Interfaces;

public interface Decoder {

	public void decode(byte[] bytes, int count);
	
	public boolean keepReading();
	
	public int getBufferSize();
	
	public byte[] getBuffer();
	
	public void reset();
	
	public String getHeader(String header);
	
	public HTTPHeaders getHeaders();
	
}