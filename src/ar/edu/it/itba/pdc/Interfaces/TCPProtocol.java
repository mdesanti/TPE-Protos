package ar.edu.it.itba.pdc.Interfaces;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

import ar.edu.it.itba.pdc.Implementations.proxy.TCPSelector;

public interface TCPProtocol {
	
      public void handleAccept(SelectionKey key) throws IOException;
      
      public void handleRead(SelectionKey key) throws IOException;
      
      public void handleWrite(SelectionKey key, Map<SocketChannel, Queue<ByteBuffer>> map) throws IOException;
      
      public void setWorker(ProxyWorker worker);
      
      public void setCaller(TCPSelector caller);
}