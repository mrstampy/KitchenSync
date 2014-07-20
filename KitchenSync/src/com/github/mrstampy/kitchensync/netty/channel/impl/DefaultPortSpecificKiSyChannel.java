package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.AbstractPortSpecificKiSyChannel;

public class DefaultPortSpecificKiSyChannel extends AbstractPortSpecificKiSyChannel {

	protected KiSyMessageProcessor messageProcessor = new KiSyMessageProcessor();
	protected DefaultChannelRegistry registry = DefaultChannelRegistry.INSTANCE;
	protected KiSyOutboundMessageManager outboundManager = KiSyOutboundMessageManager.INSTANCE;

	public DefaultPortSpecificKiSyChannel(int port) {
		super(port);
	}

	public void bind() {
		super.bind();
		registry.addChannel(this);
	}

	public void bind(int port) {
		super.bind(port);
		registry.addChannel(this);
	}

	public ChannelFuture close() {
		registry.removeChannel(this);
		return super.close();
	}

	@Override
	protected Class<? extends DatagramChannel> getChannelClass() {
		return NioDatagramChannel.class;
	}

	@Override
	protected <MSG extends Object> Object createMessage(MSG message, InetSocketAddress address) {
		return messageProcessor.createPacket(message, address);
	}

	@Override
	protected ChannelInitializer<DatagramChannel> initializer() {
		return new DefaultInitializer();
	}

	@Override
	protected <MSG> void presend(MSG message, InetSocketAddress address) {
		outboundManager.presend(message, localAddress(), address);
	}

}
