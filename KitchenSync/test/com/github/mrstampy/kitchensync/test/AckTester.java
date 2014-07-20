package com.github.mrstampy.kitchensync.test;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CountDownLatch;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultKiSyChannel;

public class AckTester extends AbstractTester {

	private DefaultKiSyChannel channel;
	private DefaultKiSyChannel channel2;

	private void execute() {
		channel = initChannel();
		channel2 = initChannel();
	}

	public void sendAckable() {
		KiSyMessage message = new KiSyMessage(channel.localAddress(), KiSyMessageType.INFO);
		KiSyMessageCreator.requireAcknowledgement(message);
		
		channel.send(message, channel2.localAddress());
	}

	protected DefaultKiSyChannel initChannel() {
		DefaultKiSyChannel channel = new DefaultKiSyChannel();

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
		AckTester ptpt = new AckTester();
		ptpt.execute();
		for (int i = 0; i < 100; i++) {
			ptpt.sendAckable();
			Thread.sleep(50);
		}
		
		ptpt.disconnect();
		System.exit(0);
	}

}
