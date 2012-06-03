package ar.edu.it.itba.pdc.v2;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Properties;

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
			Properties proxyProp = new Properties();
			FileInputStream in = new FileInputStream("proxy.properties");
			proxyProp.load(in);
			in.close();
			
			BasicConfigurator.configure();
			Logger proxy = Logger.getLogger("proxy");
			proxy.setLevel(Level.INFO);
			proxy.info("Instantiating configurator");
			Configurator configurator = new ConfiguratorImpl(Integer.valueOf(proxyProp.getProperty("configuratorPort")));
			proxy.info("Creating configurator thread");
			Thread configuratorThread = new Thread(configurator);
			Monitor monitor = new Monitor(Integer.valueOf(proxyProp.getProperty("monitorPort")));
			Thread monitorThread = new Thread(monitor);
			proxy.info("Instantiating server");
			ThreadedSocketServer server = new ThreadedSocketServer(Integer.valueOf(proxyProp.getProperty("serverPort")),
					InetAddress.getByName("localhost"), new ClientHandler(),
					configurator,monitor);
			proxy.info("Creating server thread");
			Thread serverThread = new Thread(server);

			proxy.info("Configurator thread starting");
			monitorThread.start();
			configuratorThread.start();
			proxy.info("Server thread starting");
			serverThread.start();
			monitorThread.join();
			configuratorThread.join();
			serverThread.join();
		} catch (final Exception e) {
			System.out.println("Ocurrio un error");
			e.printStackTrace();
		}
	}

}
