package com.github.mrstampy.kitchensync.message.handler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public abstract class AbstractInboundKiSyMessageHandler<MSG> implements KiSyInboundMesssageHandler<MSG> {
	private static final long serialVersionUID = -4745725747804368460L;
	private static final Logger log = LoggerFactory.getLogger(AbstractInboundKiSyMessageHandler.class);

	public static final int DEFAULT_EXECUTION_ORDER = 100;

	@Override
	public void messageReceived(MSG message, KiSyChannel<?> channel) {
		try {
			onReceive(message, channel);
		} catch (Exception e) {
			log.error("Unexpected exception processing message {}", message, e);
		}
	}

	protected abstract void onReceive(MSG message, KiSyChannel<?> channel) throws Exception;

	public final String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
