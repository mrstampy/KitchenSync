package com.github.mrstampy.kitchensync.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;

public interface KiSyChannel<CHANNEL extends DatagramChannel>  {

	boolean isActive();

	void bind(int port);

	void bind();

	<MSG extends Object> ChannelFuture send(MSG message, InetSocketAddress address);

	ChannelFuture close();

	CHANNEL getChannel();
	
	int getPort();
	
	InetSocketAddress localAddress();
}
