package ar.edu.it.itba.pdc.Implementations.monitor;

public class Decoder {
	
	private InMemoryMap map;
	
	public Decoder(InMemoryMap map) {
		this.map = map;
	}

	public String decode(String s) {
		return map.handle(s.split("\n")[0]);
	}
	
}
