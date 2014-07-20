package com.github.mrstampy.kitchensync.message.inbound.pingpong;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public abstract class PongInboundMessageHandler<MSG> extends AbstractInboundKiSyMessageHandler<MSG> {
	private static final long serialVersionUID = -8836666741037023222L;

	private PingPongMessageTimer timer = PingPongMessageTimer.TIMER;

	@Override
	public boolean canHandleMessage(MSG message) {
		if (!(message instanceof KiSyMessage)) return false;

		KiSyMessage msg = (KiSyMessage) message;

		return msg.isType(KiSyMessageType.PONG);
	}

	@Override
	protected void onReceive(MSG message, KiSyChannel<?> channel) {
		if (!(message instanceof KiSyMessage)) return;

		KiSyMessage msg = (KiSyMessage) message;

		InetSocketAddress remoteAddress = msg.createReturnAddress();

		long time = timer.pongReceived(remoteAddress);

		if (time < 0) return;

		KiSyMessage pingTime = KiSyMessageCreator.createPingTime(channel.localAddress(), remoteAddress, time);

		handlePingTimeMessage(pingTime);
	}

	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

	protected abstract void handlePingTimeMessage(KiSyMessage message);

}
