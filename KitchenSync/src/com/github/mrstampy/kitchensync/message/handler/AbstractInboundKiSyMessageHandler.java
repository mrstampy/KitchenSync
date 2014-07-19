package com.github.mrstampy.kitchensync.message.handler;

import io.netty.channel.socket.DatagramChannel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Action1;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public abstract class AbstractInboundKiSyMessageHandler<MSG, CHANNEL extends KiSyChannel<DatagramChannel, MSG>> implements
		KiSyInboundMesssageHandler<MSG, CHANNEL> {
	private static final long serialVersionUID = -4745725747804368460L;
	private static final Logger log = LoggerFactory.getLogger(AbstractInboundKiSyMessageHandler.class);

	public static final int DEFAULT_EXECUTION_ORDER = 100;

	@Override
	public void messageReceived(final MSG message, final CHANNEL channel) {
		Observable.just(message).subscribe(new Action1<MSG>() {

			@Override
			public void call(MSG t1) {
				try {
					onReceive(t1, channel);
				} catch (Exception e) {
					log.error("Unexpected exception processing message {}", t1, e);
				}
			}
		});
	}

	protected abstract void onReceive(MSG message, CHANNEL channel) throws Exception;

	public final String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
