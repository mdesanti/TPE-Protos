package ar.edu.it.itba.pdc.Interfaces;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import ar.edu.it.itba.pdc.Implementations.utils.TCPServerSelector;

public interface TCPProtocol {
	
      public void handleAccept(SelectionKey key) throws IOException;
      
      public void handleRead(SelectionKey key) throws IOException;
      
      public void handleWrite(SelectionKey key) throws IOException;
      
      public void setWorker(ProxyWorker worker);
      
      public void setCaller(TCPServerSelector caller);
}