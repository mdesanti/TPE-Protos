package ar.edu.it.itba.pdc.v2.implementations;

public class RebuiltHeader {
	
	
	private byte[] header;
	private int size;

	public RebuiltHeader(byte[] header, int size) {
		this.header = header;
		this.size = size;
	}
	
	public byte[] getHeader() {
		return header;
	}
	
	public int getSize() {
		return size;
	}
	

}
