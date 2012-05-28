package ar.edu.it.itba.pdc.v2.implementations.configurator;

import java.awt.PageAttributes.MediaType;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ws.rs.core.MediaType;

import com.sun.source.tree.Tree;

import ar.edu.it.itba.pdc.v2.interfaces.ConnectionDecoder;

public class ConfiguratorConnectionDecoder implements ConnectionDecoder {

	private boolean logged = false;
	private boolean closeConnection = false;
	private boolean applyTransformations = false;
	private boolean applyRotations =  false;
	private Map<String, String> reply;
	private Set<InetAddress> blockedAddresses;
	private Set<String> blockedMediaType;

	public ConfiguratorConnectionDecoder() {
		reply = new HashMap<String, String>();
		fillReply();
		blockedAddresses = new TreeSet<InetAddress>();
		blockedMediaType = new TreeSet<String>();
	}

	@Override
	public boolean closeConnection() {
		return closeConnection;
	}

	public String decode(String s) {
		if (!logged) {
			String[] credentials = s.split(":");
			if (credentials.length == 2 && credentials[0].equals("admin")
					&& credentials[1].equals("pdc2012")) {
				logged = true;
				return reply.get("LOG_IN_OK");
			} else {
				closeConnection = true;
				return reply.get("LOGIN_ERROR");
			}
		} else {
			String[] args = s.split(" ");
			if (args[0].equals("BLOCK")) {
				return analyzeBlockCommand(args);
			}else if(args[0].equals("UNBLOCK")){
				return analyzeUnblockCommand(args);
			} else if (args[0].equals("TRANSFORM")) {
				if (args[1].equals("ON")) {
					applyTransformations = true;
					return reply.get("TRANSF_ON");
				} else if (args[1].equals("OFF")) {
					applyTransformations = false;
					return reply.get("TRANSF_OFF");
				} else {
					return reply.get("WRONG_PARAMETERS");
				}
			} else if (args[0].equals("ROTATIONS")) {
				if (args[1].equals("ON")) {
					applyRotations = true;
					return reply.get("ROT_ON");
				} else if (args[1].equals("OFF")) {
					applyRotations = false;
					return reply.get("ROT_OFF");
				} else {
					return reply.get("WRONG_PARAMETERS");
				}
			} else if(args[0].equals("GET")) {
				if(args[1].equals("ROTATIONS")) {
					if(applyRotations) {
						return reply.get("ROT_ON");
					} else {
						return reply.get("ROT_OFF");
					}
				} else if(args[1].equals("TRANSFORMATIONS")) {
					if(applyTransformations) {
						return reply.get("TRANSF_ON");
					} else {
						return reply.get("TRANSF_OFF");
					}
				}
			}
		}
	}
	
	private String analyzeBlockCommand(String[] line) {
		String type = line[1];
		String arg = line[2];
		if(type.equals("IP")) {
			InetAddress addr = InetAddress.getByAddress(arg.getBytes());
			blockedAddresses.add(addr);
			return "200 - " + arg + " blocked\n";
		} else if(type.equals("MTYPE")) {
			blockedMediaType
		}
		
	}
	
	private String analyzeBlockCommand(String[] s){
		return null;
	}
	
	private boolean analyzeMediaType(String mtype) {
		MediaType.
	}

	private void fillReply() {
		reply.put("LOG_IN_OK", "200 - Welcome\n");
		reply.put("LOGIN_ERROR", "400 - Wrong username and/or password\n");
		reply.put("WRONG_COMMAND", "401 - Wrong command\n");
		reply.put("WRONG_PARAMETERS", "401 - Wrong parameters\n");
		reply.put("TRANSF_ON", "200 - Transformations are on\n");
		reply.put("TRANSF_OFF", "200 - Transformations are off\n");
		reply.put("ROT_ON", "200 - Rotations are on\n");
		reply.put("ROT_OFF", "200 - Rotations are off\n");
	}

}
