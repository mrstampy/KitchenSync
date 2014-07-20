package com.github.mrstampy.kitchensync.netty.channel.impl;

import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;

public class StringInboundMessageManager extends KiSyInboundMessageManager<String>{
	
	public static final StringInboundMessageManager INSTANCE = new StringInboundMessageManager();

	private StringInboundMessageManager() {
	}

}
