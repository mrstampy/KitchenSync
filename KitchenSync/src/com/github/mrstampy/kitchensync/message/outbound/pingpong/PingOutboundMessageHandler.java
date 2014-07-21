/*
 * KitchenSync Java Library Copyright (C) 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.kitchensync.message.outbound.pingpong;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.message.inbound.pingpong.PingPongMessageTimer;
import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageHandler;

/**
 * The Class PingOutboundMessageHandler starts a ping timer for each ping
 * message sent.
 *
 * @param <MSG>
 *          the generic type
 * @see PingPongMessageTimer
 */
public class PingOutboundMessageHandler<MSG> implements KiSyOutboundMessageHandler<MSG> {

	private PingPongMessageTimer timer = PingPongMessageTimer.TIMER;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageHandler
	 * #isForMessage(java.lang.Object, java.net.InetSocketAddress)
	 */
	@Override
	public boolean isForMessage(MSG message, InetSocketAddress recipient) {
		if (!(message instanceof KiSyMessage)) return false;

		KiSyMessage msg = (KiSyMessage) message;

		return msg.isType(KiSyMessageType.PING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageHandler
	 * #presend(java.lang.Object, java.net.InetSocketAddress,
	 * java.net.InetSocketAddress)
	 */
	@Override
	public void presend(MSG message, InetSocketAddress originator, InetSocketAddress recipient) {
		timer.pingSent(originator, recipient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageHandler
	 * #getExecutionOrder()
	 */
	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

}
