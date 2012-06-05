package ar.edu.it.itba.pdc.proxy.implementations.configurator.block;

import java.net.InetAddress;

public class IPBlock extends Block {

	private InetAddress ip;
	
	public IPBlock(InetAddress ip) {
		this.ip = ip;
	}
	
	public InetAddress getIp() {
		return ip;
	}
}
