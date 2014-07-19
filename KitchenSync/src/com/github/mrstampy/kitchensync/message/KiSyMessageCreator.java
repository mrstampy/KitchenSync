package com.github.mrstampy.kitchensync.message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public class KiSyMessageCreator {
	private static final String MESSAGE_ORDER = "order";
	public static final BigDecimal ONE_MILLION = new BigDecimal(1000000);
	private static final Logger log = LoggerFactory.getLogger(KiSyMessageCreator.class);
	
	private static final AtomicLong order = new AtomicLong(1);

	public static KiSyMessage createPing(KiSyChannel<?> channel) {
		return new KiSyMessage(channel.localAddress(), KiSyMessageType.PING);
	}

	public static KiSyMessage createPong(KiSyChannel<?> channel) {
		return new KiSyMessage(channel.localAddress(), KiSyMessageType.PONG);
	}

	public static KiSyMessage createPingTime(InetSocketAddress originator, InetSocketAddress destination, Long pingTime) {
		log.trace("PingTime, originator = {}, destination = {}, pingTime = {}", originator, destination, pingTime);

		KiSyMessage message = new KiSyMessage(originator, KiSyMessageType.PING_TIME);

		message.addMessage("destination", destination.toString());
		message.addMessage("pingTime", toMillis(pingTime));

		return message;
	}
	
	public static void requireAcknowledgement(KiSyMessage message) {
		message.setAckRequired(true);
		addMessageOrder(message);
	}
	
	public static void addMessageOrder(KiSyMessage message) {
		message.addMessage(MESSAGE_ORDER, Long.toString(order.getAndIncrement()));
	}
	
	public static KiSyMessage createAck(KiSyMessage ackee, KiSyChannel<?> channel) {
		KiSyMessage message = new KiSyMessage(channel.localAddress(), KiSyMessageType.ACK);
		
		String order = ackee.getMessagePart(MESSAGE_ORDER);
		if(!StringUtils.isEmpty(order)) message.addMessage(MESSAGE_ORDER, order);
		
		return message;
	}

	private static String toMillis(Long nanos) {
		return new BigDecimal(nanos).divide(ONE_MILLION, 3, RoundingMode.HALF_UP).toPlainString();
	}
}
