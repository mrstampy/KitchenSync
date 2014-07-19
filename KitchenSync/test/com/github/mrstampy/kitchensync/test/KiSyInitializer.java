package com.github.mrstampy.kitchensync.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;
import com.github.mrstampy.kitchensync.message.inbound.ack.AckInboundMessageHandler;
import com.github.mrstampy.kitchensync.message.inbound.logging.LoggingInboundMessageHandler;
import com.github.mrstampy.kitchensync.message.inbound.pingpong.PingInboundMessageHandler;
import com.github.mrstampy.kitchensync.message.inbound.pingpong.PongInboundMessageHandler;
import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageManager;
import com.github.mrstampy.kitchensync.message.outbound.logging.LoggingOutboundMessageHandler;
import com.github.mrstampy.kitchensync.message.outbound.pingpong.PingOutboundMessageHandler;

public class KiSyInitializer {
	private static final Logger log = LoggerFactory.getLogger(KiSyInitializer.class);

	public void initOutboundHandlers() {
		KiSyOutboundMessageManager manager = KiSyOutboundMessageManager.INSTANCE;

		//@formatter:off
		manager.addOutboundHandlers(
				new LoggingOutboundMessageHandler(),
				new PingOutboundMessageHandler()
				);
		//@formatter:on
	}

	@SuppressWarnings({ "serial" })
	public void initInboundHandlers() {
		KiSyInboundMessageManager<KiSyMessage> manager = KiSyInboundMessageManager.INSTANCE;

		//@formatter:off
		manager.addMessageHandlers(
				new LoggingInboundMessageHandler(),
				new PingInboundMessageHandler(),
				new PongInboundMessageHandler() {

					@Override
					protected void handlePingTimeMessage(KiSyMessage message) {
						log.info("Ping time message generated: {}", message);
					}
				},
				new AckInboundMessageHandler()
				);
		//@formatter:on
	}
}
