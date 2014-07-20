package com.github.mrstampy.kitchensync.message.inbound.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class LoggingInboundMessageHandler<MSG> extends AbstractInboundKiSyMessageHandler<MSG> {
	private static final long serialVersionUID = 345595930033076784L;
	private static final Logger log = LoggerFactory.getLogger(LoggingInboundMessageHandler.class);

	@Override
	public boolean canHandleMessage(MSG message) {
		return true;
	}

	@Override
	protected void onReceive(MSG message, KiSyChannel<?> channel) throws Exception {
		if (!log.isDebugEnabled()) return;

		String s = message.toString();
		if (s.length() > 500) s = s.substring(0, 500);

		log.debug("Received message {}", s);
	}

	@Override
	public int getExecutionOrder() {
		return 1; // logging always first
	}

}
