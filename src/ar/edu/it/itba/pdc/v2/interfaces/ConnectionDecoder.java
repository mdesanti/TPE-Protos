package ar.edu.it.itba.pdc.v2.interfaces;

import java.net.InetAddress;
import java.util.Set;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

public interface ConnectionDecoder {

	public String decode(String s);
}
