package com.github.mrstampy.kitchensync.message.inbound.ack;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class AckInboundMessageHandler extends AbstractInboundKiSyMessageHandler<KiSyMessage> {

	private static final long serialVersionUID = -4104267454467349973L;

	@Override
	public boolean canHandleMessage(KiSyMessage message) {
		return message.isAckRequired();
	}

	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

	@Override
	protected void onReceive(KiSyMessage message, KiSyChannel<?> channel) throws Exception {
		KiSyMessage ack = KiSyMessageCreator.createAck(message, channel);

		channel.send(ack, message.getReturnAddress());
	}

}
