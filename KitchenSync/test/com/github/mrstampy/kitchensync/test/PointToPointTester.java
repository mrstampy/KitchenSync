package com.github.mrstampy.kitchensync.test;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.CountDownLatch;

import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultKiSyChannel;

public class PointToPointTester extends AbstractTester {

	private DefaultKiSyChannel channel;
	private DefaultKiSyChannel channel2;

	private void execute() {
		channel = initChannel();
		channel2 = initChannel();
	}

	public void sendPing() {
		channel.send(KiSyMessageCreator.createPing(channel), channel2.localAddress());
	}

	protected DefaultKiSyChannel initChannel() {
		DefaultKiSyChannel channel = new DefaultKiSyChannel();

		channel.bind();

		return channel;
	}
	
	public void disconnect() throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(2);
		ChannelFuture cf = channel.close();
		addListener(cf, cdl);
		
		ChannelFuture cf2 = channel2.close();
		addListener(cf2, cdl);
		
		cdl.await();
	}
	
	private void addListener(ChannelFuture cf, CountDownLatch cdl) {
		cf.addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				cdl.countDown();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		PointToPointTester ptpt = new PointToPointTester();
		ptpt.execute();
		for (int i = 0; i < 100; i++) {
			ptpt.sendPing();
			Thread.sleep(50);
		}
		
		ptpt.disconnect();
		System.exit(0);
	}

}
