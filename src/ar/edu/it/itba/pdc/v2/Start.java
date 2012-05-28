package ar.edu.it.itba.pdc.v2;

import java.net.InetAddress;

import ar.edu.it.itba.pdc.v2.implementations.configurator.ConfiguratorImpl;
import ar.edu.it.itba.pdc.v2.implementations.proxy.ClientHandler;
import ar.edu.it.itba.pdc.v2.implementations.proxy.ThreadedSocketServer;
import ar.edu.it.itba.pdc.v2.interfaces.Configurator;

public class Start {

	public static void main(String args[]) {
		try {
			Configurator configurator = new ConfiguratorImpl();
			Thread configuratorThread = new Thread(configurator);
			// Monitor monitor = new Monitor();
			// Thread monitorThread = new Thread(monitor);
			ThreadedSocketServer server = new ThreadedSocketServer(9090,
					InetAddress.getByName("localhost"), new ClientHandler(),
					configurator);
			Thread serverThread = new Thread(server);

			configuratorThread.start();
			serverThread.start();
			configuratorThread.join();
			serverThread.join();
		} catch (final Exception e) {
			System.out.println("Ocurrio un error");
			e.printStackTrace();
		}
	}

}
