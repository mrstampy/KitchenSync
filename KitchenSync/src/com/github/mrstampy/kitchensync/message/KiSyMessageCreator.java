package com.github.mrstampy.kitchensync.message;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.message.coretypes.DefaultReturnAddress;

public class KiSyMessageCreator {
	private static final Logger log = LoggerFactory.getLogger(KiSyMessageCreator.class);

	public static KiSyMessage createPing(InetAddress address, int port) {
		KiSyMessage message = new KiSyMessage(KiSyMessageType.PING);

		DefaultReturnAddress dra = new DefaultReturnAddress(address, port);

		message.addMessage("ipa", dra.getIpa());
		message.addMessage("port", Integer.toString(dra.getPort()));

		return message;
	}

	public static KiSyMessage createPong(InetAddress address, int port) {
		KiSyMessage message = new KiSyMessage(KiSyMessageType.PONG);

		DefaultReturnAddress dra = new DefaultReturnAddress(address, port);

		message.addMessage("ipa", dra.getIpa());
		message.addMessage("port", Integer.toString(dra.getPort()));

		return message;
	}

	public static KiSyMessage createPingTime(InetSocketAddress originator, InetSocketAddress destination, Long pingTime) {
		log.trace("PingTime, originator = {}, destination = {}, pingTime = {}", originator, destination, pingTime);

		KiSyMessage message = new KiSyMessage(KiSyMessageType.PING_TIME);

		message.addMessage("destination", destination.toString());
		message.addMessage("originator", originator.toString());
		message.addMessage("pingTime", pingTime.toString());

		return message;
	}
}
