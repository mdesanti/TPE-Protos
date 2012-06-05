package ar.edu.it.itba.pdc.proxy.implementations.configurator.block;

import nl.bitwalker.useragentutils.OperatingSystem;

public class OSBlock extends Block {

	private OperatingSystem OS;
	
	public OSBlock(OperatingSystem OS) {
		super();
		this.OS = OS;
	}
	
	public OperatingSystem getOS() {
		return OS;
	}
}
