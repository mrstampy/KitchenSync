package com.github.mrstampy.kitchensync.message.outbound.logging;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageHandler;

public class LoggingOutboundMessageHandler<MSG> implements KiSyOutboundMessageHandler<MSG> {
	private static final Logger log = LoggerFactory.getLogger(LoggingOutboundMessageHandler.class);

	@Override
	public boolean isForMessage(MSG message, InetSocketAddress recipient) {
		return true;
	}

	@Override
	public void presend(MSG message, InetSocketAddress originator, InetSocketAddress recipient) {
		log.debug("Sending {} from {} to {}", message, originator, recipient);
	}

	@Override
	public int getExecutionOrder() {
		return 1;
	}

}
