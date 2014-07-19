package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.handler.KiSyInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.kitchensync.netty.handler.JsonMessageHandler;

@SuppressWarnings("rawtypes")
public class DefaultInitializer<CHANNEL extends KiSyChannel<DatagramChannel, KiSyMessage>> extends
		ChannelInitializer<DatagramChannel> {

	private CHANNEL channel;

	public DefaultInitializer(CHANNEL channel) {
		this.channel = channel;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new JsonMessageHandler(KiSyInboundMessageManager.INSTANCE, channel));
	}

}
