package com.github.mrstampy.kitchensync.netty.channel.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

import com.github.mrstampy.kitchensync.netty.handler.ByteArrayMessageHandler;

public class ByteArrayMessageInitializer extends ChannelInitializer<DatagramChannel> {

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new ByteArrayMessageHandler());
	}

}
