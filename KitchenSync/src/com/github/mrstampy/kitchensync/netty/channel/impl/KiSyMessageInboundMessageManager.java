package com.github.mrstampy.kitchensync.netty.channel.impl;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;

public class KiSyMessageInboundMessageManager extends KiSyInboundMessageManager<KiSyMessage> {

	public static KiSyMessageInboundMessageManager INSTANCE = new KiSyMessageInboundMessageManager();
	
	private KiSyMessageInboundMessageManager() {
	}

}
