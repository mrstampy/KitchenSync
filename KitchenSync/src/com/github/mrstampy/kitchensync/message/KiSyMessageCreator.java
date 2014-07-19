package com.github.mrstampy.kitchensync.message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class KiSyMessageCreator {
	public static final BigDecimal ONE_MILLION = new BigDecimal(1000000);
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

		message.addMessage("pingTime", toMillis(pingTime));

		return message;
	}

	private static String toMillis(Long nanos) {
		return new BigDecimal(nanos).divide(ONE_MILLION, 3, RoundingMode.HALF_UP).toPlainString();
	}
}
