package ar.edu.it.itba.pdc.proxy.implementations.monitor.interfaces;

public interface ConnectionDecoder {
	/**
	 * Analyze an input string according to the Configurator protocol and
	 * returns a status code and message
	 * 
	 * @param s
	 * @return
	 */
	public String decode(String s);
}
