package ar.edu.it.itba.pdc.Implementations;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import ar.edu.it.itba.pdc.Implementations.utils.DataEvent;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;
import ar.edu.it.itba.pdc.Interfaces.TCPProtocol;

public abstract class TCPSelector implements Runnable {

	
	protected static final int BUFSIZE = 10 * 1024; // Buffer size (bytes)
	protected static final int TIMEOUT = 1000; // Wait timeout (milliseconds)

	protected int port;
	protected ProxyWorker worker;
	protected TCPProtocol protocol;
	protected Queue<DataEvent> queue;
	/**
	 * This map associates a specific socket channel with a queue of buffers to send. A certain socket
	 * might receive several packets to send. Thats why this queue is needed
	 * */
	protected Map<SocketChannel, Queue<ByteBuffer>> map = new HashMap<SocketChannel, Queue<ByteBuffer>>();

	public TCPSelector(ProxyWorker worker, int port, TCPProtocol protocol) {
		this.port = port;
		this.worker = worker;
		this.protocol = protocol;
		protocol.setWorker(worker);
		queue = new LinkedList<DataEvent>();
	}
	
	public void processEvent(DataEvent event) {
		synchronized (queue) {
			queue.add(event);
			
		}
	}

}
