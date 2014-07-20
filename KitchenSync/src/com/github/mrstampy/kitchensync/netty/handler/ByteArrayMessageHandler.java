package com.github.mrstampy.kitchensync.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteArrayMessageHandler extends AbstractKiSyNettyHandler {
	private static final Logger log = LoggerFactory.getLogger(ByteArrayMessageHandler.class);

	public ByteArrayMessageHandler() {
		super(HandlerType.BYTE_ARRAY);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		byte[] b = bytes(msg);

		try {
			processMessage(b, msg);
		} catch (Exception e) {
			log.error("Could not process {}", b, e);
		}
	}

}
