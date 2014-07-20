package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

import com.github.mrstampy.kitchensync.netty.handler.KiSyMessageHandler;

public class DefaultKiSyMessageInitializer extends ChannelInitializer<DatagramChannel> {

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new KiSyMessageHandler());
	}

}
