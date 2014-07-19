package com.github.mrstampy.kitchensync.message;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class KiSyMessageCreator {
	private static final Logger log = LoggerFactory.getLogger(KiSyMessageCreator.class);

	public static KiSyMessage createPing(KiSyChannel<?> channel) {
		return new KiSyMessage(channel.localAddress(), KiSyMessageType.PING);
	}

	public static KiSyMessage createPong(KiSyChannel<?> channel) {
		return new KiSyMessage(channel.localAddress(), KiSyMessageType.PONG);
	}

	public static KiSyMessage createPingTime(InetSocketAddress originator, InetSocketAddress destination, Long pingTime) {
		log.trace("PingTime, originator = {}, destination = {}, pingTime = {}", originator, destination, pingTime);

		KiSyMessage message = new KiSyMessage(originator, KiSyMessageType.PING_TIME);

		message.addMessage("pingTime", pingTime.toString());

		return message;
	}
}
