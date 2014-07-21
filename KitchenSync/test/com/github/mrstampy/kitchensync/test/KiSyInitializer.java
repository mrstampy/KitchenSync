/*
 * KitchenSync Java Library Copyright (C) 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.kitchensync.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.inbound.ByteArrayInboundMessageManager;
import com.github.mrstampy.kitchensync.message.inbound.KiSyMessageInboundMessageManager;
import com.github.mrstampy.kitchensync.message.inbound.StringInboundMessageManager;
import com.github.mrstampy.kitchensync.message.inbound.ack.AckInboundMessageHandler;
import com.github.mrstampy.kitchensync.message.inbound.logging.LoggingInboundMessageHandler;
import com.github.mrstampy.kitchensync.message.inbound.pingpong.PingInboundMessageHandler;
import com.github.mrstampy.kitchensync.message.inbound.pingpong.PongInboundMessageHandler;
import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageManager;
import com.github.mrstampy.kitchensync.message.outbound.logging.LoggingOutboundMessageHandler;
import com.github.mrstampy.kitchensync.message.outbound.pingpong.PingOutboundMessageHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class KiSyInitializer.
 */
@SuppressWarnings("rawtypes")
public class KiSyInitializer {
	private static final Logger log = LoggerFactory.getLogger(KiSyInitializer.class);

	/**
	 * Inits the outbound handlers.
	 */
	public void initOutboundHandlers() {
		KiSyOutboundMessageManager manager = KiSyOutboundMessageManager.INSTANCE;

		//@formatter:off
		manager.addOutboundHandlers(
				new LoggingOutboundMessageHandler(),
				new PingOutboundMessageHandler()
				);
		//@formatter:on
	}

	/**
	 * Inits the inbound handlers.
	 */
	@SuppressWarnings({ "serial" })
	public void initInboundHandlers() {
		KiSyMessageInboundMessageManager manager = KiSyMessageInboundMessageManager.INSTANCE;

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
		
		StringInboundMessageManager.INSTANCE.addMessageHandlers(new LoggingInboundMessageHandler());
		ByteArrayInboundMessageManager.INSTANCE.addMessageHandlers(new LoggingInboundMessageHandler());
		//@formatter:on
	}
}
