package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyMulticastChannel;

public class DefaultKiSyMulticastChannel extends AbstractKiSyMulticastChannel<KiSyMessage> {

	protected KiSyMessageProcessor messageProcessor = new KiSyMessageProcessor();
	
	protected KiSyOutboundMessageManager outboundManager = KiSyOutboundMessageManager.INSTANCE;

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ChannelInitializer<DatagramChannel> initializer() {
		return new DefaultInitializer(this);
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
