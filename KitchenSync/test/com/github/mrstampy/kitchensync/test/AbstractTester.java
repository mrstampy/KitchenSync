package com.github.mrstampy.kitchensync.test;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractTester {

	protected AbstractTester() {
		initKiSyHandlers();
	}

	protected void initKiSyHandlers() {
		KiSyInitializer initer = new KiSyInitializer();

		initer.initInboundHandlers();
		initer.initOutboundHandlers();
	}
	
	protected void addLatchListener(ChannelFuture cf, final CountDownLatch cdl) {
		cf.addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				cdl.countDown();
			}
		});
	}

}
