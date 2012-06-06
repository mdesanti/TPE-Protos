package ar.edu.it.itba.pdc.proxy.implementations.proxy;import java.io.IOException;import java.net.InetAddress;import java.net.ServerSocket;import java.net.Socket;import java.util.concurrent.ExecutorService;import java.util.concurrent.Executors;import org.apache.log4j.Level;import org.apache.log4j.Logger;import ar.edu.it.itba.pdc.proxy.implementations.configurator.interfaces.Configurator;import ar.edu.it.itba.pdc.proxy.implementations.monitor.implementations.Monitor;import ar.edu.it.itba.pdc.proxy.interfaces.ConnectionManager;public class ThreadedSocketServer implements Runnable {	private ServerSocket serverSocket;	private ConnectionManager connectionManager;	private Configurator configurator;	private Monitor monitor;	public ThreadedSocketServer(final int port, final InetAddress interfaz,			Configurator configurator, Monitor monitor,			ConnectionManager connectionManager) throws IOException {		init(new ServerSocket(port, 50, interfaz), configurator, monitor,				connectionManager);	}	private void init(final ServerSocket s, final Configurator configurator,			final Monitor monitor, final ConnectionManager connectionManager) {		if (s == null || configurator == null || monitor == null) {			throw new IllegalArgumentException();		}		this.serverSocket = s;		this.monitor = monitor;		this.connectionManager = connectionManager;		this.configurator = configurator;	}	public void run() {		Logger server = Logger.getLogger(this.getClass());		server.setLevel(Level.INFO);		server.info("Proxy listenting on " + serverSocket.getLocalSocketAddress());		ExecutorService es = Executors.newCachedThreadPool();		while (true) {			Socket socket;			try {				socket = this.serverSocket.accept();				connectionManager.registerClientConnection(socket);				monitor.getDataStorage().addClientOpenConnection(1);				server.info("Connection accepted from "						+ socket.getInetAddress());				es.execute(new Attend(socket, connectionManager, configurator,						monitor));			} catch (IOException e) {				return;			}		}	}}