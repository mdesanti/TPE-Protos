package ar.edu.it.itba.pdc.Implementations.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

import ar.edu.it.itba.pdc.Implementations.TCPSelector;
import ar.edu.it.itba.pdc.Interfaces.ProxyWorker;

public class Worker implements ProxyWorker{

	private Queue<DataEvent> queue = new LinkedList<DataEvent>();
	private TCPSelector server;
	private TCPSelector client;
	private BufferedWriter logger;

	public Worker() {
		FileWriter logger = null;
		try {
			logger = new FileWriter("/tmp/" + this.getClass().getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.logger = new BufferedWriter(logger);
	}
	
	@Override
	public void run() {
		if(server == null || client == null)
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
			TCPSelector rcpt = dataEvent.getSender().equals(server)?client:server;
			rcpt.processEvent(dataEvent);
		}

	}

	@Override
	public void sendData(TCPSelector sender, SocketChannel from,
			byte[] data, long count) {
		byte[] copy = new byte[(int) count];
		System.arraycopy(data, 0, copy, 0, (int) count);
		synchronized (queue) {
			queue.add(new DataEvent(sender, from, copy));
			queue.notify();
		}

	}
	
	@Override
	public void setServer(TCPSelector server) {
		this.server = server;
	}
	
	@Override
	public void setClient(TCPSelector client) {
		this.client = client;
	}

}
