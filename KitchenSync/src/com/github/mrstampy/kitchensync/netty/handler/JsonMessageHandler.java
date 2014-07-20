package com.github.mrstampy.kitchensync.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mrstampy.kitchensync.message.KiSyMessage;

public class JsonMessageHandler extends AbstractKiSyNettyHandler {
	private static final Logger log = LoggerFactory.getLogger(JsonMessageHandler.class);

	private ObjectMapper mapper = new ObjectMapper();

	public JsonMessageHandler() {
		super(HandlerType.KISY_MESSAGE);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		String json = content(msg);
		log.debug("Received {} from {}", json, msg.sender());

		try {
			processMessage(mapper.readValue(json, KiSyMessage.class), msg);
		} catch (Exception e) {
			log.error("Could not process {}", json, e);
		}
	}

}
