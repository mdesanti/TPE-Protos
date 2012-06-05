package ar.edu.it.itba.pdc.proxy.implementations.monitor.implementations;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.edu.it.itba.pdc.proxy.implementations.monitor.exceptions.BadCredentialException;
import ar.edu.it.itba.pdc.proxy.implementations.monitor.interfaces.ConnectionDecoder;
import ar.edu.it.itba.pdc.proxy.implementations.monitor.interfaces.DataStorage;

public class MonitorDecoder implements ConnectionDecoder {

	private Map<String, String> options;
	private DataStorage storage = DataStorageImpl.getInstance();
	private Logger configLog;

	public MonitorDecoder() {
		fillOptions();
		configLog= Logger.getLogger(this.getClass());
		configLog.setLevel(Level.INFO);
	}

	public DataStorage getStorage() {
		return storage;
	}

	public String decode(String s) {

		try {
			s = s.replace("\n", "");

			if (s.equals("HELP"))
				return printHelp();

			String[] args = s.split(" ");
			String method = args[0];
			if (args.length == 1) {
				if (!method.equals("EXIT")){
					return options.get("BAD_REQUEST");
				}else {
					configLog.info("EXIT command received. Closing connection");
					return null;
				}
			}
			String option = args[1];
			String filter = args[2];

			if (!method.equals("GET")) {
				return options.get("BAD_REQUEST");
			}
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

		if (filter.equals("ALL"))
			return this.options.get("OK") + "\ncomando-bytes(ALL)="
					+ storage.getTotalBytes() + "\n";
		if (filter.equals("CP"))
			return this.options.get("OK") + "\n comando-bytes(CP)="
					+ storage.getClientProxyBytes() + "\n";
		if (filter.equals("PS"))
			return this.options.get("OK") + "\n comando-bytes(CS)="
					+ storage.getProxyServersBytes() + "\n";

		return this.options.get("BAD_REQUEST");

	}

	private String analizeComandoCount(String filter) {
		if (filter.equals("BLOCK"))
			return this.options.get("OK") + "\ncomando-count(BLOCK)="
					+ storage.getBlocks() + "\n";
		if (filter.equals("TRANS"))
			return this.options.get("OK") + "\ncomando-count(TRANS)="
					+ storage.getTransformations() + "\n";

		return this.options.get("BAD_REQUEST");
	}

	private String analizeComandoCons(String filter) {
		if (filter.equals("C"))
			return this.options.get("OK") + "\ncomando-cons(C)="
					+ storage.getClientOpenConnections() + "\n";
		if (filter.equals("S"))
			return this.options.get("OK") + "\ncomando-count(S)="
					+ storage.getServersOpenConnections() + "\n";

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

	public String logIn(String s) {
		try {
			s = s.replace("\n", "");
			String[] userAndPass = s.split(":");
			String user = userAndPass[0];
			String pass = userAndPass[1];

			if (user.equals("admin") && pass.equals("123")){
				configLog.info("User is now logged.");
				return "Hello, you may use \"HELP\" to list commands\n";
			}
		} catch (Exception e) {

			throw new BadCredentialException();
		}
		throw new BadCredentialException();

	}

	private String printHelp() {
		StringBuffer sb = new StringBuffer();
		sb.append("command:comando-bytes|comando-count|comando-cons\n");
		sb.append("comando-bytes=\"GET\"SP\"BYTES\"SP(ALL|CP|PS)\n");
		sb.append("comando-count=\"GET\"SP\"COUNT\"SP(BLOCK|TRANS)\n");
		sb.append("comando-cons=\"GET\"SP\"CONS\"SP(C|S)\n");

		return sb.toString();
	}

}
