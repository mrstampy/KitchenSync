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
package com.github.mrstampy.kitchensync.netty.channel.payload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mrstampy.kitchensync.message.KiSyMessage;

/**
 * Converts a {@link KiSyMessage} to a ByteBuf object containing a Json string
 * representation of the {@link KiSyMessage} object.
 */
public class KiSyMessageByteBufCreator implements ByteBufCreator {
	private static final Logger log = LoggerFactory.getLogger(KiSyMessageByteBufCreator.class);

	private ObjectMapper mapper = new ObjectMapper();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.netty.channel.payload.ByteBufCreator#
	 * isForMessage(java.lang.Object, java.net.InetSocketAddress)
	 */
	@Override
	public <MSG> boolean isForMessage(MSG message, InetSocketAddress recipient) {
		return message instanceof KiSyMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.netty.channel.payload.ByteBufCreator#
	 * createBytes(java.lang.Object, java.net.InetSocketAddress)
	 */
	@Override
	public <MSG> ByteBuf createByteBuf(MSG message, InetSocketAddress recipient) {
		try {
			String s = mapper.writeValueAsString(message);
			return Unpooled.copiedBuffer(s, CharsetUtil.UTF_8);
		} catch (JsonProcessingException e) {
			log.error("Could not return KiSyMessage Json string for {}", message, e);
		}

		return null;
	}

}
