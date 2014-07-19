package com.github.mrstampy.kitchensync.message.outbound.pingpong;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.message.handler.pingpong.PingPongMessageTimer;
import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageHandler;

public class PingOutboundMessageHandler implements KiSyOutboundMessageHandler<KiSyMessage> {

	private PingPongMessageTimer timer = PingPongMessageTimer.TIMER;
	
	@Override
	public boolean isForMessage(KiSyMessage message, InetSocketAddress recipient) {
		return message.isType(KiSyMessageType.PING);
	}

	@Override
	public void presend(KiSyMessage message, InetSocketAddress originator, InetSocketAddress recipient) {
		timer.pingSent(originator, recipient);
	}

	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

}
