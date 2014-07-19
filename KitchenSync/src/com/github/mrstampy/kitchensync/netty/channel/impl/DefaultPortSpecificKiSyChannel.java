package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.netty.channel.AbstractPortSpecificKiSyChannel;

public abstract class DefaultPortSpecificKiSyChannel extends
		AbstractPortSpecificKiSyChannel<DatagramChannel, KiSyMessage> {

	protected KiSyMessageProcessor messageProcessor = new KiSyMessageProcessor();
	protected DefaultChannelRegistry registry = DefaultChannelRegistry.INSTANCE;

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
	protected Object createMessage(KiSyMessage message, InetSocketAddress address) {
		return messageProcessor.createPacket(message, address);
	}

}
