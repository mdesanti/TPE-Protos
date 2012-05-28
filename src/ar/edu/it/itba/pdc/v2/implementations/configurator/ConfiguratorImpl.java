package ar.edu.it.itba.pdc.v2.implementations.configurator;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import ar.edu.it.itba.pdc.v2.implementations.monitor.DataStorageImpl;
import ar.edu.it.itba.pdc.v2.interfaces.Configurator;
import ar.edu.it.itba.pdc.v2.interfaces.DataStorage;

public class ConfiguratorImpl implements Configurator {

	private int port = 9092;
	private static int maxMessageLength = 512;

	private ConfiguratorConnectionHandler handler;
	private ConfiguratorConnectionDecoder decoder;

	public ConfiguratorImpl() {
		this.decoder = new ConfiguratorConnectionDecoder();
		this.handler = new ConfiguratorConnectionHandler(maxMessageLength,
				decoder);
	}

	public void run() {
		ServerSocket socketServer = null;
		try {
			socketServer = new ServerSocket(port);
		} catch (IOException e) {
			System.out
					.printf("Could not initiate Configurator. Proxy will run without it. Please check that the port %d is available\n",
							port);
			return;
		}

		while (true) {
			Socket socket;
			try {
				socket = socketServer.accept();
				handler.handle(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean applyRotations() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean applyTextTransformation() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getMaxSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isAccepted(InetAddress addr) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAccepted(String str) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAccepted(MediaType str) {
		// TODO Auto-generated method stub
		return false;
	}
}
