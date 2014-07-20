package com.github.mrstampy.kitchensync.message.outbound.pingpong;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.message.inbound.pingpong.PingPongMessageTimer;
import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageHandler;

public class PingOutboundMessageHandler<MSG> implements KiSyOutboundMessageHandler<MSG> {

	private PingPongMessageTimer timer = PingPongMessageTimer.TIMER;

	@Override
	public boolean isForMessage(MSG message, InetSocketAddress recipient) {
		if(!(message instanceof KiSyMessage)) return false;
		
		KiSyMessage msg = (KiSyMessage)message;

		return msg.isType(KiSyMessageType.PING);
	}

	@Override
	public void presend(MSG message, InetSocketAddress originator, InetSocketAddress recipient) {
		timer.pingSent(originator, recipient);
	}

	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

}
