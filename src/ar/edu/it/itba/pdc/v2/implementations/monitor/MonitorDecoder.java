package ar.edu.it.itba.pdc.v2.implementations.monitor;

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

		try {
			String[] args = s.split(" ");
			String method = args[0];
			String option = args[1];
			String filter = args[2];

			if (!method.equals("GET"))
				return options.get("BAD_REQUEST");
			if (option.equals("BYTES"))
				return analizeComandoBytes(filter);
			if (option.equals("COUNT"))
				return analizeComandoCount(filter);
			if (option.equals("CONS"))
				return analizeComandoCons(filter);

			return options.get("BAD_REQUEST");
		} catch (Exception e) {
			return options.get("BAD_REQUEST");
		}
	}

	private String analizeComandoBytes(String filter) {
		try {

			if (filter.equals("ALL\n"))
				return this.options.get("OK") + "\ncomando-bytes(ALL)="
						+ storage.getTotalBytes() + "\n";
			if (filter.equals("CP\n"))
				return this.options.get("OK") + "\n comando-bytes(CP)="
						+ storage.getClientProxyBytes() + "\n";
			if (filter.equals("PS\n"))
				return this.options.get("OK") + "\n comando-bytes(CS)="
						+ storage.getProxyServersBytes() + "\n";

			return this.options.get("BAD_REQUEST");
		} catch (NullPointerException e) {

			return this.options.get("BAD_REQUEST");

		}
	}

	private String analizeComandoCount(String filter) {
		if (filter.equals("BLOCK\n"))
			return this.options.get("OK") + "\ncomando-count(BLOCK)="
					+ storage.getBlocks() + "\n";
		if (filter.equals("TRANS\n"))
			return this.options.get("OK") + "\ncomando-count(TRANS)="
					+ storage.getTransformations() + "\n";

		return this.options.get("BAD_REQUEST");
	}

	private String analizeComandoCons(String filter) {
		if (filter.equals("C\n"))
			return this.options.get("OK") + "\ncomando-cons(C)="
					+ storage.getClientOpenConections() + "\n";
		if (filter.equals("S\n"))
			return this.options.get("OK") + "\ncomando-count(S)="
					+ storage.getServersOpenConections() + "\n";

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
