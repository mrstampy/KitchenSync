package com.github.mrstampy.kitchensync.test;

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

	public static void main(String[] args) {
		PointToPointTester ptpt = new PointToPointTester();
		ptpt.execute();
		ptpt.sendPing();
	}

}
