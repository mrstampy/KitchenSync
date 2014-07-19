package com.github.mrstampy.kitchensync.message.coretypes;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.mrstampy.kitchensync.message.KiSyMessage;

public class DefaultReturnAddress implements ReturnAddress {
	private static final long serialVersionUID = -960234209220918406L;

	private String ipa;
	private int port;

	public DefaultReturnAddress() {
	}

	public DefaultReturnAddress(InetAddress address, int port) {
		this(address.getHostAddress(), port);
	}

	public DefaultReturnAddress(String ipa, int port) {
		setIpa(ipa);
		setPort(port);
	}
	
	public DefaultReturnAddress(KiSyMessage message) {
		setIpa(message.getMessagePart("ipa"));
		setPort(Integer.parseInt(message.getMessagePart("port")));
	}

	@Override
	public int getPort() {
		return port;
	}

	public String getIpa() {
		return ipa;
	}

	public void setIpa(String ipa) {
		this.ipa = ipa;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@JsonIgnore
	public InetSocketAddress createFrom() {
		return new InetSocketAddress(getIpa(), getPort());
	}

}
