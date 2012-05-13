package ar.edu.it.itba.pdc.Implementations.monitor;

import java.util.HashMap;
import java.util.Map;

public class KeyValueMap implements InMemoryMap {

	private final String invalidParams = "Invalid parameters\n";
	private Map<String, Integer> map;

	public KeyValueMap() {
		this.map = new HashMap<String, Integer>();
	}

	public int getValue(String key) {
		Integer ret = map.get(key);
		if (ret != null)
			return ret;
		else
			throw new IllegalArgumentException();
	}

	public boolean increase(String key, int qty) {
		Integer value = map.get(key);
		if (value == null)
			return false;

		map.put(key, value + qty);
		return true;
	};

	public boolean setValue(String key, int value) {
		map.put(key, value);
		return true;
	}

	@Override
	public String handle(String s) {
		String[] commands = s.split(" ");
		if (commands.length == 2) {
			if (!commands[0].equals("get"))
				return invalidParams;
			int ret = 0;
			try {
				ret = getValue(commands[1]);
			} catch (IllegalArgumentException e) {
				return invalidParams;
			}
			return String.valueOf(ret) + "\n";
		} else if (commands.length == 3) {
			if (commands[0].equals("increase")) {
				int qty = 0;
				try {
					qty = Integer.parseInt(commands[2]);
				} catch (Exception e) {
					return invalidParams;
				}
				if (increase(commands[1], qty))
					return "Now " + commands[1] + " has value: "
							+ String.valueOf(getValue(commands[1]) + "\n");
			} else if (commands[0].equals("decrease")) {
				int qty = 0;
				try {
					qty = Integer.parseInt(commands[2]);
				} catch (Exception e) {
					return invalidParams;
				}
				if (increase(commands[1], -qty))
					return "Now " + commands[1] + " has value: "
							+ String.valueOf(getValue(commands[1])) + "\n";
			} else if (commands[0].equals("set")) {
				int qty = 0;
				try {
					qty = Integer.parseInt(commands[2]);
				} catch (Exception e) {
					return invalidParams;
				}
				if (setValue(commands[1], qty))
					return "Now " + commands[1] + " has value: "
							+ String.valueOf(getValue(commands[1])) + "\n";
			}
		} else {
			return invalidParams;
		}
		return s;

	}
}
