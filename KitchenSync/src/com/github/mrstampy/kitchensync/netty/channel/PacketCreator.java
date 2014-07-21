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
package com.github.mrstampy.kitchensync.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mrstampy.kitchensync.message.KiSyMessage;

/**
 * The Class PacketCreator converts a message into a DatagramPacket.
 */
public class PacketCreator {
	private static final Logger log = LoggerFactory.getLogger(PacketCreator.class);

	private ObjectMapper jsonMapper = new ObjectMapper();

	/**
	 * Creates the packet.
	 *
	 * @param <MSG>
	 *          the generic type
	 * @param message
	 *          the message
	 * @param recipient
	 *          the recipient
	 * @return the object
	 */
	public <MSG extends Object> Object createPacket(MSG message, InetSocketAddress recipient) {
		if (message instanceof KiSyMessage) return new DatagramPacket(getBuf((KiSyMessage) message), recipient);

		if (message instanceof byte[]) return new DatagramPacket(getBuf((byte[]) message), recipient);

		return new DatagramPacket(getBuf(message.toString()), recipient);
	}

	/**
	 * Gets the buf.
	 *
	 * @param message
	 *          the message
	 * @return the buf
	 */
	protected ByteBuf getBuf(String message) {
		return Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
	}

	/**
	 * Gets the buf.
	 *
	 * @param message
	 *          the message
	 * @return the buf
	 */
	protected ByteBuf getBuf(byte[] message) {
		return Unpooled.copiedBuffer(message);
	}

	/**
	 * Gets the buf.
	 *
	 * @param message
	 *          the message
	 * @return the buf
	 */
	protected ByteBuf getBuf(KiSyMessage message) {
		return getBuf(toJson(message));
	}

	/**
	 * To json.
	 *
	 * @param message
	 *          the message
	 * @return the string
	 */
	protected String toJson(KiSyMessage message) {
		try {
			return jsonMapper.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			log.error("Unexpected error unmarshalling {}", message, e);
			throw new RuntimeException(e);
		}
	}
}
