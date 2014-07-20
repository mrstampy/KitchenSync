package com.github.mrstampy.kitchensync.netty.channel.impl;

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

public class PacketCreator {
	private static final Logger log = LoggerFactory.getLogger(PacketCreator.class);

	private ObjectMapper jsonMapper = new ObjectMapper();

	public <MSG extends Object> Object createPacket(MSG message, InetSocketAddress recipient) {
		if (message instanceof KiSyMessage) return new DatagramPacket(getBuf((KiSyMessage) message), recipient);

		if (message instanceof byte[]) return new DatagramPacket(getBuf((byte[]) message), recipient);

		return new DatagramPacket(getBuf(message.toString()), recipient);
	}

	protected ByteBuf getBuf(String message) {
		return Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
	}

	protected ByteBuf getBuf(byte[] message) {
		return Unpooled.copiedBuffer(message);
	}

	protected ByteBuf getBuf(KiSyMessage message) {
		return getBuf(toJson(message));
	}

	protected String toJson(KiSyMessage message) {
		try {
			return jsonMapper.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			log.error("Unexpected error unmarshalling {}", message, e);
			throw new RuntimeException(e);
		}
	}
}
