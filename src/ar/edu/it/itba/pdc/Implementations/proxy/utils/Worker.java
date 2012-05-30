package ar.edu.it.itba.pdc.Implementations.proxy.utils;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

import ar.edu.it.itba.pdc.Implementations.proxy.TCPSelector;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;

public class Worker implements ProxyWorker {

	private Queue<DataEvent> queue = new LinkedList<DataEvent>();
	private TCPSelector server;
	private TCPSelector client;

	public Worker() {
	}

	public void run() {
		if (server == null || client == null)
			throw new IllegalThreadStateException();
		DataEvent dataEvent;

		while (true) {
			// Wait for data to become available
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = queue.remove();
			}
			TCPSelector rcpt = dataEvent.getSender().equals(server) ? client
					: server;
			rcpt.processEvent(dataEvent);
		}

	}

	public void sendData(TCPSelector sender, SocketChannel from, byte[] data,
			long count, boolean multiPart) {
		byte[] copy = new byte[(int) count];
		System.arraycopy(data, 0, copy, 0, (int) count);
		synchronized (queue) {
			queue.add(new DataEvent(sender, from, copy, multiPart));
			queue.notify();
		}

	}

	public void setServer(TCPSelector server) {
		this.server = server;
	}

	public void setClient(TCPSelector client) {
		this.client = client;
	}

}
