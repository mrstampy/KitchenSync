package com.github.mrstampy.kitchensync.message.inbound;

public class ByteArrayInboundMessageManager extends KiSyInboundMessageManager<byte[]> {

	public static final ByteArrayInboundMessageManager INSTANCE = new ByteArrayInboundMessageManager();

	private ByteArrayInboundMessageManager() {
	}

}
