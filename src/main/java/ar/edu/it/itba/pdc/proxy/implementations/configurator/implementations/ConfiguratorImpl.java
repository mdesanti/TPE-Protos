package ar.edu.it.itba.pdc.proxy.implementations.configurator.implementations;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.ws.rs.core.MediaType;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.proxy.implementations.configurator.interfaces.Configurator;

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
		Logger configLog = Logger.getLogger(this.getClass());
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

	
	public boolean applyRotationsFor(Browser b, OperatingSystem os, InetAddress ip) {
		return handler.applyRotations(os) || handler.applyRotations(b) || handler.applyRotations(ip);
	}

	public boolean applyTextTransformationFor(Browser b, OperatingSystem os, InetAddress ip) {
		return handler.applyTextTransformation(b) || handler.applyTextTransformation(os) || handler.applyTextTransformation(ip);
	}

	public boolean applyTransformationFor(Browser b, OperatingSystem os, InetAddress ip) {
		return applyRotationsFor(b, os, ip) || applyTextTransformationFor(b, os, ip);
	}

	public int getMaxSize(Browser b, OperatingSystem os, InetAddress ip) {
		return handler.getMaxSize(b, os, ip);
	}

	public boolean isAccepted(InetAddress addr, Browser b, OperatingSystem os, InetAddress ip) {
		return handler.isAccepted(addr, b, os, ip);
	}

	public boolean isAccepted(String str, Browser b, OperatingSystem os, InetAddress ip) {
		return handler.isAccepted(str, b, os, ip);
	}

	public boolean isAccepted(MediaType str, Browser b, OperatingSystem os, InetAddress ip) {
		return handler.isAccepted(str, b, os, ip);
	}
	
	public boolean blockAll(Browser b, OperatingSystem os, InetAddress ip) {
		return decoder.blockAllFor(b) && decoder.blockAllFor(ip) && decoder.blockAllFor(ip);
	}

}
