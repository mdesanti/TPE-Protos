package ar.edu.it.itba.pdc.v2.implementations.configurator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.v2.interfaces.Configurator;

public class ConfiguratorImpl implements Configurator {

	private int port = 9092;
	private static int maxMessageLength = 512;

	private ConfiguratorConnectionHandler handler;
	private ConfiguratorConnectionDecoder decoder;

	public ConfiguratorImpl(int port) {
		this.decoder = new ConfiguratorConnectionDecoder();
		this.handler = new ConfiguratorConnectionHandler(maxMessageLength,
				decoder);
		this.port = port;
	}

	public void run() {
		Logger configLog = Logger.getLogger("proxy.configurator");
		configLog.setLevel(Level.INFO);
		ServerSocket socketServer = null;
		try {
			socketServer = new ServerSocket(port);
			configLog.info("Configurator is listenting on port " + port);
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
				configLog.info("Accepted connection from " + socket.getInetAddress());
				handler.handle(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean applyRotations() {
		return handler.applyRotations();
	}

	public boolean applyTextTransformation() {
		return handler.applyTextTransformation();
	}

	public boolean applyTransformation() {
		return applyRotations() || applyTextTransformation();
	}

	public int getMaxSize() {
		return handler.getMaxSize();
	}

	public boolean isAccepted(InetAddress addr) {
		return handler.isAccepted(addr);
	}

	public boolean isAccepted(String str) {
		return handler.isAccepted(str);
	}

	public boolean isAccepted(MediaType str) {
		return handler.isAccepted(str);
	}
	
	public boolean blockAll() {
		return decoder.blockAll();
	}
}
