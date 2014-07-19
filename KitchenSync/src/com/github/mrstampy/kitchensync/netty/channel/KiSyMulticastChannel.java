package com.github.mrstampy.kitchensync.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;

public interface KiSyMulticastChannel extends KiSyChannel<DatagramChannel> {

	<MSG> ChannelFuture broadcast(MSG message);

	void multicastBind();

	boolean joinGroup();

	boolean leaveGroup();

	InetSocketAddress getMulticastAddress();

	NetworkInterface getNetworkInterface();
}
