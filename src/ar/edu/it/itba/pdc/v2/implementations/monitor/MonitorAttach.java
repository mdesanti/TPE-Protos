package ar.edu.it.itba.pdc.v2.implementations.monitor;

import java.nio.ByteBuffer;

public class MonitorAttach {

	ByteBuffer buf;
	boolean logged;

	MonitorAttach(ByteBuffer buf) {
		this.buf = buf;
		logged = false;
	}

	public boolean getLogged() {
		return this.logged;
	}

	public void logIn() {
		this.logged = true;
	}

	public void logOut() {
		this.logged = false;
	}

	public ByteBuffer getBuffer() {
		return this.buf;
	}
}
