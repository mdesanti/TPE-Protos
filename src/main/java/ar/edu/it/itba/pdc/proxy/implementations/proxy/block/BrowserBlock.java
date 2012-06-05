package ar.edu.it.itba.pdc.proxy.implementations.proxy.block;

import nl.bitwalker.useragentutils.Browser;

public class BrowserBlock extends Block {

	private Browser browser;
	
	public BrowserBlock(Browser browser) {
		this.browser = browser;
	}
	
	public Browser getBrowser() {
		return browser;
	}
}
