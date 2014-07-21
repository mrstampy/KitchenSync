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
package com.github.mrstampy.kitchensync.message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

/**
 * Provides convenience methods for {@link KiSyMessage} creation.
 */
public class KiSyMessageCreator {
	private static final String MESSAGE_ORDER = "order";

	/** The Constant ONE_MILLION. */
	public static final BigDecimal ONE_MILLION = new BigDecimal(1000000);
	private static final Logger log = LoggerFactory.getLogger(KiSyMessageCreator.class);

	private static final AtomicLong order = new AtomicLong(1);

	/**
	 * Creates the ping message.
	 *
	 * @param channel
	 *          the channel
	 * @return the ki sy message
	 */
	public static KiSyMessage createPing(KiSyChannel channel) {
		return new KiSyMessage(channel.localAddress(), KiSyMessageType.PING);
	}

	/**
	 * Creates the pong message.
	 *
	 * @param channel
	 *          the channel
	 * @return the ki sy message
	 */
	public static KiSyMessage createPong(KiSyChannel channel) {
		return new KiSyMessage(channel.localAddress(), KiSyMessageType.PONG);
	}

	/**
	 * Creates the ping time message.
	 *
	 * @param originator
	 *          the originator
	 * @param destination
	 *          the destination
	 * @param pingTime
	 *          the ping time
	 * @return the ki sy message
	 */
	public static KiSyMessage createPingTime(InetSocketAddress originator, InetSocketAddress destination, Long pingTime) {
		log.trace("PingTime, originator = {}, destination = {}, pingTime = {}", originator, destination, pingTime);

		KiSyMessage message = new KiSyMessage(originator, KiSyMessageType.PING_TIME);

		message.addMessagePart("destination", destination.toString());
		message.addMessagePart("pingTime", toMillis(pingTime));

		return message;
	}

	/**
	 * Sets the specified message to require acknowledgement of receipt.
	 *
	 * @param message
	 *          the message
	 */
	public static void requireAcknowledgement(KiSyMessage message) {
		message.setAckRequired(true);
		addMessageOrder(message);
	}

	/**
	 * Adds the message order. This value autoincrements therefore ordering is
	 * relative.
	 *
	 * @param message
	 *          the message
	 */
	public static void addMessageOrder(KiSyMessage message) {
		message.addMessagePart(MESSAGE_ORDER, Long.toString(order.getAndIncrement()));
	}

	/**
	 * Creates the ack message.
	 *
	 * @param ackee
	 *          the message requesting the ack
	 * @param channel
	 *          the channel
	 * @return the ki sy message
	 */
	public static KiSyMessage createAck(KiSyMessage ackee, KiSyChannel channel) {
		KiSyMessage message = new KiSyMessage(channel.localAddress(), KiSyMessageType.ACK);

		String order = ackee.getMessagePart(MESSAGE_ORDER);
		if (!StringUtils.isEmpty(order)) message.addMessagePart(MESSAGE_ORDER, order);

		return message;
	}

	/**
	 * Converts the specified time in nanoseconds to its string value in
	 * milliseconds, to 3 decimal places.
	 *
	 * @param nanos
	 *          the nanos
	 * @return the string
	 */
	public static String toMillis(Long nanos) {
		return new BigDecimal(nanos).divide(ONE_MILLION, 3, RoundingMode.HALF_UP).toPlainString();
	}
}
