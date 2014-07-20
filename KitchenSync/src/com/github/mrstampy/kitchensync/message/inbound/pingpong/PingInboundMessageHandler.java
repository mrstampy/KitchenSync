package com.github.mrstampy.kitchensync.message.inbound.pingpong;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class PingInboundMessageHandler<MSG> extends AbstractInboundKiSyMessageHandler<MSG> {
	private static final long serialVersionUID = -3848676913366352139L;

	@Override
	public boolean canHandleMessage(MSG message) {
		if (!(message instanceof KiSyMessage)) return false;

		KiSyMessage msg = (KiSyMessage) message;

		return msg.isType(KiSyMessageType.PING);
	}

	@Override
	protected void onReceive(MSG message, KiSyChannel<?> channel) {
		if (!(message instanceof KiSyMessage)) return;

		KiSyMessage msg = (KiSyMessage) message;

		KiSyMessage pong = KiSyMessageCreator.createPong(channel);

		channel.send(pong, msg.getReturnAddress());
	}

	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

}
