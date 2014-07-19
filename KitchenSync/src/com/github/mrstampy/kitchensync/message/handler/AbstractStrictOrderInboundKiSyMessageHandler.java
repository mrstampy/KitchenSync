package com.github.mrstampy.kitchensync.message.handler;

import io.netty.channel.socket.DatagramChannel;

import java.util.concurrent.locks.Lock;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

public abstract class AbstractStrictOrderInboundKiSyMessageHandler<MSG, CHANNEL extends KiSyChannel<DatagramChannel, MSG>>
		extends AbstractInboundKiSyMessageHandler<MSG, CHANNEL> {

	private static final long serialVersionUID = -8783098166337384378L;

	private MessageLocker locker = new MessageLocker();

	protected void onReceive(MSG message, CHANNEL channel) throws Exception {
		Lock lock = locker.getLock(message);
		lock.lock();
		try {
			onReceive0(message, channel);
		} finally {
			lock.unlock();
		}
	}

	protected abstract void onReceive0(MSG message, CHANNEL channel) throws Exception;
}
