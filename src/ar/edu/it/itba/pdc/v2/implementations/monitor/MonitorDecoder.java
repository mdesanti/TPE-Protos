package ar.edu.it.itba.pdc.v2.implementations.monitor;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import ar.edu.it.itba.pdc.v2.interfaces.ConnectionDecoder;
import ar.edu.it.itba.pdc.v2.interfaces.DataStorage;

public class MonitorDecoder implements ConnectionDecoder {

	private final String invalidParams = "Invalid parameters\n";
	private Map<String, String> options;
	private DataStorage storage = DataStorageImpl.getInstance();

	public MonitorDecoder() {
		fillOptions();
	}

	public String decode(String s) {
		String[] args = s.split("\n");
		String[] commandUsed = (args[0]).split("=");
		String[] commandOpt = (args[1]).split("=");
		if (!commandUsed[0].equals("comando"))
			return options.get("BAD_REQUEST");
		if (commandUsed[1].equals(commandOpt[0]))
			return options.get("PARAM_MATCH");
		if (commandUsed[1].equals("comando-bytes"))
			return analizeComandoBytes(commandOpt[1]);
		if (commandUsed[1].equals("comando-count"))
			return analizeComandoCount(commandOpt[1]);
		if (commandUsed[1].equals("comando-cons"))
			return analizeComandoCons(commandOpt[1]);

		return null;
	}

	private String analizeComandoBytes(String options) {
		try {
			String[] args = options.split(" ");
			if (!(args[0].equals("GET")) | !(args[1].equals("BYTES")))
				return this.options.get("BAD_REQUEST");
			if (args[2].equals("ALL"))
				return this.options.get("OK") + "\ncomando-bytes="
						+ storage.getTotalBytes();
			if (args[2].equals("CP"))
				return this.options.get("OK") + "\n comando-bytes="
						+ storage.getClientProxyBytes();
			if (args[2].equals("PS"))
				return this.options.get("OK") + "\n comando-bytes="
						+ storage.getProxyServersBytes();

			return this.options.get("BAD_REQUEST");
		} catch (NullPointerException e) {

			return this.options.get("BAD_REQUEST");

		}
	}

	private String analizeComandoCount(String options) {
		String[] args = options.split(" ");
		if (!(args[0].equals("GET")) | !(args[1].equals("COUNT")))
			return this.options.get("BAD_REQUEST");
		if (args[2].equals("BLOCK"))
			return this.options.get("OK") + "\ncomando-count="
					+ storage.getBlocks();
		if (args[2].equals("TRANS"))
			return this.options.get("OK") + "\ncomando-count="
					+ storage.getTransformations();

		return this.options.get("BAD_REQUEST");
	}

	private String analizeComandoCons(String options) {
		String[] args = options.split(" ");
		if (!(args[0].equals("GET")) | !(args[1].equals("CONS")))
			return this.options.get("BAD_REQUEST");
		if (args[2].equals("C"))
			return this.options.get("OK") + "\ncomando-cons="
					+ storage.getClientOpenConections();
		if (args[2].equals("S"))
			return this.options.get("OK") + "\ncomando-count="
					+ storage.getServersOpenConections();

		return this.options.get("BAD_REQUEST");

	}

	private void fillOptions() {
		this.options = new HashMap<String, String>();
		options.put("LOG_IN_OK", "200 - Welcome\n");
		options.put("LOGIN_ERROR", "400 - Wrong username and/or password\n");
		options.put("BAD_REQUEST", "401 - Bad request\n");
		options.put("PARAM_MATCH",
				"401 - The specified command does not match the used command\n");
		options.put("OK", "200 - OK");
	}

	public boolean closeConnection() {
		return false;
	}

}
