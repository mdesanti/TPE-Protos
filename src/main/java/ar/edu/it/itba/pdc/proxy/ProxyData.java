package ar.edu.it.itba.pdc.proxy;

import java.net.InetAddress;

public class ProxyData {

	private int configPort = -1;
	private int serverPort = -1;
	private int monitorPort = -1;
	private boolean intermProxy = false;
	private InetAddress intermProxyAddr = null;
	private int intermProxyPort = -1;

	public ProxyData(int configPort, int serverPort, int monitorPort,
			boolean intermProxy, InetAddress intermProxyAddr,
			int intermProxyPort) {
		this.configPort = configPort;
		this.serverPort = serverPort;
		this.monitorPort = monitorPort;
		this.intermProxy = intermProxy;
		this.intermProxyAddr = intermProxyAddr;
		this.intermProxyPort = intermProxyPort;
	}

	public int getConfigPort() {
		return configPort;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getMonitorPort() {
		return monitorPort;
	}

	public int getIntermProxyPort() {
		return intermProxyPort;
	}

	public InetAddress getIntermProxyAddr() {
		return intermProxyAddr;
	}

	public boolean extistsIntermediateProxy() {
		return intermProxy;
	}
}
