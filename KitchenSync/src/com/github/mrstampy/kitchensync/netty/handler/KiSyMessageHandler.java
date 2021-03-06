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
package com.github.mrstampy.kitchensync.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.inbound.KiSyMessageInboundMessageManager;

/**
 * This class marshals the Json string in the message to a {@link KiSyMessage}
 * and sends it off for processing.
 */
public class KiSyMessageHandler extends AbstractKiSyNettyHandler<KiSyMessage> {
	private static final Logger log = LoggerFactory.getLogger(KiSyMessageHandler.class);

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * The Constructor.
	 */
	public KiSyMessageHandler() {
		super(KiSyMessageInboundMessageManager.INSTANCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel
	 * .ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		String json = content(msg);

		try {
			processMessage(mapper.readValue(json, KiSyMessage.class), msg);
		} catch (Exception e) {
			log.error("Could not process {}", json, e);
		}
	}

}
