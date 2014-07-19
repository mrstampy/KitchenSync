package com.github.mrstampy.kitchensync.message.handler.pingpong;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.message.handler.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class PingMessageHandler extends AbstractInboundKiSyMessageHandler<KiSyMessage> {
	private static final long serialVersionUID = -3848676913366352139L;

	@Override
	public boolean canHandleMessage(KiSyMessage message) {
		return message.isType(KiSyMessageType.PING);
	}

	@Override
	protected void onReceive(KiSyMessage message, KiSyChannel<?> channel) {
		KiSyMessage pong = KiSyMessageCreator.createPong(channel);

		channel.send(pong, message.createReturnAddress());
	}

	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

}
