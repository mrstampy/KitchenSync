package com.github.mrstampy.kitchensync.netty.channel.impl;

import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;

public class StringInboundMessageHandler extends KiSyInboundMessageManager<String>{
	
	public static final StringInboundMessageHandler INSTANCE = new StringInboundMessageHandler();

	private StringInboundMessageHandler() {
	}

}
