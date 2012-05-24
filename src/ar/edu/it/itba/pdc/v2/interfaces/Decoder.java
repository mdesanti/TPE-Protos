package ar.edu.it.itba.pdc.v2.interfaces;


public interface Decoder {

	public void decode(byte[] bytes, int count);
	
	public boolean keepReading();
	
	public int getBufferSize();
	
	public String getHeader(String header);
	
	public void applyTransformations(byte[] bytes, int count);
	
	public void applyFilters();

	public void applyRestrictions(byte[] bytes, int count);
	
	public boolean completeHeaders(byte[] bytes, int count);
	
}