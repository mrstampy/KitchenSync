package com.github.mrstampy.kitchensync.message.handler;

import io.netty.channel.socket.DatagramChannel;

import java.io.Serializable;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public interface KiSyInboundMesssageHandler<MSG, CHANNEL extends KiSyChannel<DatagramChannel, MSG>> extends Serializable {

	boolean canHandleMessage(MSG message);

	void messageReceived(MSG message, CHANNEL channel) throws Exception;

	int getExecutionOrder();
}
