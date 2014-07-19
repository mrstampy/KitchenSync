package com.github.mrstampy.kitchensync.message.handler.pingpong;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.message.handler.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public abstract class PongMessageHandler extends AbstractInboundKiSyMessageHandler<KiSyMessage> {
	private static final long serialVersionUID = -8836666741037023222L;

	private PingPongMessageTimer timer = PingPongMessageTimer.TIMER;

	@Override
	public boolean canHandleMessage(KiSyMessage message) {
		return message.isType(KiSyMessageType.PONG);
	}

	@Override
	protected void onReceive(KiSyMessage message, KiSyChannel<?> channel) {
		InetSocketAddress remoteAddress = message.createReturnAddress();

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
