package com.github.mrstampy.kitchensync.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringMessageHandler extends AbstractKiSyNettyHandler {
	private static final Logger log = LoggerFactory.getLogger(StringMessageHandler.class);

	public StringMessageHandler() {
		super(HandlerType.STRING);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		String s = content(msg);

		try {
			processMessage(s, msg);
		} catch (Exception e) {
			log.error("Could not process {}", s, e);
		}
	}

}
