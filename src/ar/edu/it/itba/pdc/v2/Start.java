package ar.edu.it.itba.pdc.v2;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.v2.implementations.configurator.ConfiguratorImpl;
import ar.edu.it.itba.pdc.v2.implementations.monitor.Monitor;
import ar.edu.it.itba.pdc.v2.implementations.proxy.ClientHandler;
import ar.edu.it.itba.pdc.v2.implementations.proxy.ThreadedSocketServer;
import ar.edu.it.itba.pdc.v2.interfaces.Configurator;

public class Start {

	public static void main(String args[]) {
		try {
			BasicConfigurator.configure();
			Logger proxy = Logger.getLogger("proxy");
			proxy.setLevel(Level.INFO);
			proxy.info("Instantiating configurator");
			Configurator configurator = new ConfiguratorImpl();
			proxy.info("Creating configurator thread");
			Thread configuratorThread = new Thread(configurator);
			Monitor monitor = new Monitor();
			Thread monitorThread = new Thread(monitor);
			proxy.info("Instantiating server");
			ThreadedSocketServer server = new ThreadedSocketServer(9090,
					InetAddress.getByName("localhost"), new ClientHandler(),
					configurator);
			proxy.info("Creating server thread");
			Thread serverThread = new Thread(server);

			proxy.info("Configurator thread starting");
			configuratorThread.start();
			proxy.info("Server thread starting");
			serverThread.start();
			monitorThread.start();
			configuratorThread.join();
			serverThread.join();
			monitorThread.join();
		} catch (final Exception e) {
			System.out.println("Ocurrio un error");
			e.printStackTrace();
		}
	}

}
