package com.github.mrstampy.kitchensync.message.inbound;

import java.io.Serializable;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public interface KiSyInboundMesssageHandler<MSG> extends Serializable {

	boolean canHandleMessage(MSG message);

	void messageReceived(MSG message, KiSyChannel<?> channel) throws Exception;

	int getExecutionOrder();
}
