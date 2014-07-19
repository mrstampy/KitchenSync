package com.github.mrstampy.kitchensync.message.handler.logging;

import io.netty.channel.socket.DatagramChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.handler.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class LoggingMessageHandler<CHANNEL extends KiSyChannel<DatagramChannel, KiSyMessage>> extends
		AbstractInboundKiSyMessageHandler<KiSyMessage, CHANNEL> {
	private static final long serialVersionUID = 345595930033076784L;
	private static final Logger log = LoggerFactory.getLogger(LoggingMessageHandler.class);

	@Override
	public boolean canHandleMessage(KiSyMessage message) {
		return true;
	}

	@Override
	protected void onReceive(KiSyMessage message, CHANNEL channel) throws Exception {
		log.debug("Received message {}", message);
	}

	@Override
	public int getExecutionOrder() {
		return 1; // logging always first
	}

}
