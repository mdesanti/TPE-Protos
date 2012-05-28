package ar.edu.it.itba.pdc.v2.implementations.configurator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ar.edu.it.itba.pdc.v2.interfaces.ConnectionHandler;

public class Configurator implements Runnable {

	private int port = 9092;
	private static int maxMessageLength = 512;
	
	private ConnectionHandler handler;

	public Configurator(ConnectionHandler handler) {
		this.handler = handler;
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
		
		while(true) {
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
}
