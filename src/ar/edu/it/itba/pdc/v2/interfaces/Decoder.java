package ar.edu.it.itba.pdc.v2.interfaces;

import java.io.IOException;

import ar.edu.it.itba.pdc.v2.implementations.HTML;
import ar.edu.it.itba.pdc.v2.implementations.RebuiltHeader;

public interface Decoder {


	public boolean keepReading();


	public String getHeader(String header);



	public void applyRestrictions(byte[] bytes, int count,
			HTTPHeaders requestHeader);

	public byte[] getRotatedImage() throws IOException;

	public byte[] getTransformed();

	public boolean completeHeaders(byte[] bytes, int count);

	public void reset();

	public boolean parseHeaders(byte[] data, int count,String action);

	public HTTPHeaders getHeaders();

	public byte[] getExtra(byte[] data, int count);

	public void analize(byte[] bytes, int count);

	public RebuiltHeader rebuildHeaders();
	
	public RebuiltHeader rebuildResponseHeaders();

	public void setConfigurator(Configurator configurator);

	public boolean applyTransformations();

	public boolean isImage();

	public boolean isText();

	public RebuiltHeader generateBlockedHeader(String cause);
	
	public HTML generateBlockedHTML(String cause);

	public RebuiltHeader modifiedContentLength(int contentLength);
	
	public boolean contentExpected();
	
}
