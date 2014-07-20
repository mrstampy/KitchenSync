package com.github.mrstampy.kitchensync.test;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CountDownLatch;

import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultPortSpecificKiSyChannel;

public class PingPongTester extends AbstractTester {

	private DefaultPortSpecificKiSyChannel channel;
	private DefaultPortSpecificKiSyChannel channel2;

	private void execute() {
		channel = initChannel(56789);
		channel2 = initChannel(56790);
	}

	public void sendPing() {
		channel.send(KiSyMessageCreator.createPing(channel), channel2.localAddress());
	}

	protected DefaultPortSpecificKiSyChannel initChannel(int port) {
		DefaultPortSpecificKiSyChannel channel = new DefaultPortSpecificKiSyChannel(port);

		channel.bind();

		return channel;
	}

	public void disconnect() throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(2);
		ChannelFuture cf = channel.close();
		addLatchListener(cf, cdl);

		ChannelFuture cf2 = channel2.close();
		addLatchListener(cf2, cdl);

		cdl.await();
	}

	public static void main(String[] args) throws Exception {
		PingPongTester ptpt = new PingPongTester();
		ptpt.execute();
		for (int i = 0; i < 100; i++) {
			ptpt.sendPing();
			Thread.sleep(50);
		}

		ptpt.disconnect();
		System.exit(0);
	}

}
