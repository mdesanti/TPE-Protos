package ar.edu.it.itba.pdc.proxy.implementations.configurator.block;

import java.net.InetAddress;
import java.util.Set;
import java.util.TreeSet;

public class Block {
	private Set<InetAddress> blockedAddresses;
	private Set<String> blockedMediaType;
	private Set<String> blockedURIs;
	private boolean applyTransformations = false;
	private boolean applyRotations = false;
	private boolean blockAll = false;
	private int maxSize = -1;
	
	public Block() {
		blockedAddresses = new TreeSet<InetAddress>();
		blockedMediaType = new TreeSet<String>();
		blockedURIs = new TreeSet<String>();
	}
	
	public void addBlockedAddress(InetAddress blocked) {
		blockedAddresses.add(blocked);
	}
	
	public void addBlockedMType(String mtype) {
		blockedMediaType.add(mtype);
	}
	
	public void addBlockedURI(String regex) {
		blockedURIs.add(regex);
	}
	
	public Set<InetAddress> getBlockedAddresses() {
		return blockedAddresses;
	}
	
	public Set<String> getBlockedMediaType() {
		return blockedMediaType;
	}
	
	public Set<String> getBlockedURIs() {
		return blockedURIs;
	}
	
	public boolean isApplyTransformations() {
		return applyTransformations;
	}
	
	public boolean isBlockAll() {
		return blockAll;
	}
	
	public boolean isApplyRotations() {
		return applyRotations;
	}
	
	public void setApplyRotations(boolean applyRotations) {
		this.applyRotations = applyRotations;
	}
	
	public void setBlockAll(boolean blockAll) {
		this.blockAll = blockAll;
	}
	
	public void setApplyTransformations(boolean applyTransformations) {
		this.applyTransformations = applyTransformations;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public void removeBlockedURI(String uri) {
		blockedURIs.remove(uri);
	}
	
	public void removeBlockedIP(InetAddress addr) {
		blockedAddresses.remove(addr);
	}
	
	public void removeBlockedMType(String mtype) {
		blockedMediaType.remove(mtype);
	}
	
}
