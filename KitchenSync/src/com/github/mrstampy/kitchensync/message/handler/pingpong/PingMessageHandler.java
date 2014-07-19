package com.github.mrstampy.kitchensync.message.handler.pingpong;

import io.netty.channel.socket.DatagramChannel;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.message.coretypes.DefaultReturnAddress;
import com.github.mrstampy.kitchensync.message.handler.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class PingMessageHandler<CHANNEL extends KiSyChannel<DatagramChannel, KiSyMessage>> extends
		AbstractInboundKiSyMessageHandler<KiSyMessage, CHANNEL> {
	private static final long serialVersionUID = -3848676913366352139L;

	@Override
	public boolean canHandleMessage(KiSyMessage message) {
		return message.isType(KiSyMessageType.PING);
	}

	@Override
	protected void onReceive(KiSyMessage message, CHANNEL channel) {
		KiSyMessage pong = KiSyMessageCreator.createPong(channel.localAddress().getAddress(), channel.localAddress().getPort());

		DefaultReturnAddress returnAddress = new DefaultReturnAddress(message);

		channel.send(pong, returnAddress.createFrom());
	}

	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

}
