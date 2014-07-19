package com.github.mrstampy.kitchensync.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.netty.Bootstrapper;
import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultKiSyChannel;

public class PointToPointTester extends AbstractTester {

	private DefaultKiSyChannel channel;

	private void execute() {
		channel = initChannel();
	}
	
	public void sendPing(InetAddress address, int port) {
		channel.send(KiSyMessageCreator.createPing(address, channel.getPort()), new InetSocketAddress(address, port));
	}

	protected DefaultKiSyChannel initChannel() {
		DefaultKiSyChannel channel = new DefaultKiSyChannel();

		channel.bind();

		return channel;
	}

	public static void main(String[] args) {
		PointToPointTester ptpt = new PointToPointTester();
		ptpt.execute();
		ptpt.sendPing(Bootstrapper.DEFAULT_ADDRESS, 55109);
	}

}
