package com.github.mrstampy.kitchensync.message.inbound.ack;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class AckInboundMessageHandler<MSG> extends AbstractInboundKiSyMessageHandler<MSG> {

	private static final long serialVersionUID = -4104267454467349973L;

	@Override
	public boolean canHandleMessage(MSG message) {
		if (!(message instanceof KiSyMessage)) return false;

		KiSyMessage msg = (KiSyMessage) message;

		return msg.isAckRequired();
	}

	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

	@Override
	protected void onReceive(MSG message, KiSyChannel<?> channel) throws Exception {
		if (!(message instanceof KiSyMessage)) return;

		KiSyMessage msg = (KiSyMessage) message;

		KiSyMessage ack = KiSyMessageCreator.createAck(msg, channel);

		channel.send(ack, msg.getReturnAddress());
	}

}
