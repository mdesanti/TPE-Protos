package ar.edu.it.itba.pdc.v2.interfaces;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionHandler {

    public void handle(Socket socket) throws IOException;
    
}