package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.netty.Bootstrapper;
import com.github.mrstampy.kitchensync.netty.channel.AbstractPortSpecificKiSyChannel;

public abstract class DefaultPortSpecificKiSyChannel extends AbstractPortSpecificKiSyChannel<DatagramChannel, KiSyMessage> {
	
	protected KiSyMessageProcessor messageProcessor = new KiSyMessageProcessor();

	public DefaultPortSpecificKiSyChannel(int port) {
		super(port);
	}
	
	public DefaultPortSpecificKiSyChannel(int port, Bootstrapper bootstrapper) {
		super(port, bootstrapper);
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
