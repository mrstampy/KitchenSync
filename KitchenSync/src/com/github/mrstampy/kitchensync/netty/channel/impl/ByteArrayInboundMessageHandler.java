package com.github.mrstampy.kitchensync.netty.channel.impl;

import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;

public class ByteArrayInboundMessageHandler extends KiSyInboundMessageManager<byte[]>{
	
	public static final ByteArrayInboundMessageHandler INSTANCE = new ByteArrayInboundMessageHandler();

	private ByteArrayInboundMessageHandler() {
	}

}
