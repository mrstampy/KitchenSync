package com.github.mrstampy.kitchensync.message.inbound;

import com.github.mrstampy.kitchensync.message.KiSyMessage;

public class KiSyMessageInboundMessageManager extends KiSyInboundMessageManager<KiSyMessage> {

	public static KiSyMessageInboundMessageManager INSTANCE = new KiSyMessageInboundMessageManager();

	private KiSyMessageInboundMessageManager() {
	}

}
