package ar.edu.it.itba.pdc.v2.interfaces;

import ar.edu.it.itba.pdc.v2.implementations.RebuiltHeader;

public interface Decoder {

	public void decode(byte[] bytes, int count);

	public boolean keepReading();

	public int getBufferSize();

	public String getHeader(String header);

	public void applyTransformations(byte[] bytes, int count);

	public void applyFilters();

	public void applyRestrictions(byte[] bytes, int count,
			HTTPHeaders requestHeader);

	public byte[] getRotatedImage();

	public byte[] getTransformed();

	public boolean completeHeaders(byte[] bytes, int count);

	public void reset();

	public void parseHeaders(byte[] data, int count);

	public HTTPHeaders getHeaders();

	public byte[] getExtra(byte[] data, int count);

	public void analize(byte[] bytes, int count);

	public RebuiltHeader rebuildHeaders();

	public void setConfigurator(Configurator configurator);
	public boolean applyTransformations();
	public boolean isImage();
	public boolean isText();

}
