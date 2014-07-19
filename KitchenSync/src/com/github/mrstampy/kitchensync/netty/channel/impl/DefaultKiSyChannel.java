package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyChannel;

public class DefaultKiSyChannel extends AbstractKiSyChannel<DatagramChannel, KiSyMessage> {
	protected KiSyMessageProcessor messageProcessor = new KiSyMessageProcessor();

	protected KiSyOutboundMessageManager outboundManager = KiSyOutboundMessageManager.INSTANCE;
	protected DefaultChannelRegistry registry = DefaultChannelRegistry.INSTANCE;

	public DefaultKiSyChannel() {
		super();
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
	protected ChannelInitializer<DatagramChannel> initializer() {
		return new DefaultInitializer();
	}

	@Override
	protected Class<? extends DatagramChannel> getChannelClass() {
		return NioDatagramChannel.class;
	}

	@Override
	protected Object createMessage(KiSyMessage message, InetSocketAddress address) {
		return messageProcessor.createPacket(message, address);
	}

	@Override
	protected void presend(KiSyMessage message, InetSocketAddress address) {
		outboundManager.presend(message, localAddress(), address);
	}

}
