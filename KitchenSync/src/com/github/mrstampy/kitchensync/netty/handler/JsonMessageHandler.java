package com.github.mrstampy.kitchensync.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.handler.KiSyInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultChannelRegistry;

public class JsonMessageHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private static final Logger log = LoggerFactory.getLogger(JsonMessageHandler.class);

	private ObjectMapper mapper = new ObjectMapper();
	private KiSyInboundMessageManager<KiSyMessage> handlerManager = KiSyInboundMessageManager.INSTANCE;
	private DefaultChannelRegistry registry = DefaultChannelRegistry.INSTANCE;

	public JsonMessageHandler(KiSyInboundMessageManager<KiSyMessage> handlerManager) {
		this.handlerManager = handlerManager;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		String json = msg.content().toString(CharsetUtil.UTF_8);
		log.debug("Received {} from {}", json, msg.sender());

		try {
			KiSyMessage message = mapper.readValue(json, KiSyMessage.class);

			handlerManager.processMessage(message, registry.getChannel(msg.recipient().getPort()));
		} catch (Exception e) {
			log.error("Could not process {}", json, e);
		}
	}

}
