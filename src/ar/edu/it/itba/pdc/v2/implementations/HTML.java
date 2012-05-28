package ar.edu.it.itba.pdc.v2.implementations;

public class HTML {

	private byte[] html;
	private int size;

	public HTML(byte[] header, int size) {
		this.html = header;
		this.size = size;
	}
	
	public byte[] getHTML() {
		return html;
	}
	
	public int getSize() {
		return size;
	}
}
