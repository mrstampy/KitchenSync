package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyMulticastChannel;

public abstract class DefaultKiSyMulticastChannel extends AbstractKiSyMulticastChannel {

	protected KiSyMessageProcessor messageProcessor = new KiSyMessageProcessor();

	protected KiSyOutboundMessageManager outboundManager = KiSyOutboundMessageManager.INSTANCE;
	protected DefaultChannelRegistry registry = DefaultChannelRegistry.INSTANCE;

	public DefaultKiSyMulticastChannel(String multicastIPv6, int port) throws UnknownHostException {
		super(multicastIPv6, port);
	}

	public DefaultKiSyMulticastChannel(String multicastIPv6, int port, NetworkInterface networkInterface)
			throws UnknownHostException {
		super(multicastIPv6, port, networkInterface);
	}

	public DefaultKiSyMulticastChannel(InetSocketAddress multicastAddress) {
		super(multicastAddress);
	}

	public DefaultKiSyMulticastChannel(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
		super(multicastAddress, networkInterface);
	}

	public void bind() {
		super.bind();
		registry.addChannel(this);
	}

	public void bind(int port) {
		super.bind(port);
		registry.addChannel(this);
	}

	public void multicastBind() {
		super.multicastBind();
		registry.addMulticastChannel(this);
	}

	public ChannelFuture close() {
		registry.removeChannel(this);
		registry.removeMulticastChannel(this);

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
	protected <MSG extends Object> void presend(MSG message, InetSocketAddress address) {
		outboundManager.presend(message, localAddress(), address);
	}

}
