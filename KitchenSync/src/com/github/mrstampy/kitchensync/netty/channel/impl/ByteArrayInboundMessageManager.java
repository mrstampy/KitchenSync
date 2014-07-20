package com.github.mrstampy.kitchensync.netty.channel.impl;

import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;

public class ByteArrayInboundMessageManager extends KiSyInboundMessageManager<byte[]>{
	
	public static final ByteArrayInboundMessageManager INSTANCE = new ByteArrayInboundMessageManager();

	private ByteArrayInboundMessageManager() {
	}

}
