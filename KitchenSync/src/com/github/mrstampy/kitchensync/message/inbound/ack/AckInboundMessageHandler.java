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
package com.github.mrstampy.kitchensync.message.inbound.ack;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

/**
 * The Class AckInboundMessageHandler creates an acknowledgement and sends it
 * back to the originator.
 *
 * @param <MSG>
 *          the generic type
 */
public class AckInboundMessageHandler<MSG> extends AbstractInboundKiSyMessageHandler<MSG> {

	private static final long serialVersionUID = -4104267454467349973L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMesssageHandler
	 * #canHandleMessage(java.lang.Object)
	 */
	@Override
	public boolean canHandleMessage(MSG message) {
		if (!(message instanceof KiSyMessage)) return false;

		KiSyMessage msg = (KiSyMessage) message;

		return msg.isAckRequired();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMesssageHandler
	 * #getExecutionOrder()
	 */
	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.message.inbound.
	 * AbstractInboundKiSyMessageHandler#onReceive(java.lang.Object,
	 * com.github.mrstampy.kitchensync.netty.channel.KiSyChannel)
	 */
	@Override
	protected void onReceive(MSG message, KiSyChannel<?> channel) throws Exception {
		if (!(message instanceof KiSyMessage)) return;

		KiSyMessage msg = (KiSyMessage) message;

		KiSyMessage ack = KiSyMessageCreator.createAck(msg, channel);

		channel.send(ack, msg.getReturnAddress());
	}

}
