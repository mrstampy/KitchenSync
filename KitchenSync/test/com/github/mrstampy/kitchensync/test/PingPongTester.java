/*
 * KitchenSync Java Library Copyright (C) 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.kitchensync.test;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.CountDownLatch;

import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.netty.channel.AbstractPortSpecificKiSyChannel;
import com.github.mrstampy.kitchensync.netty.channel.initializer.KiSyMessageInitializer;

// TODO: Auto-generated Javadoc
/**
 * The Class PingPongTester.
 */
public class PingPongTester extends AbstractTester {

	private AbstractPortSpecificKiSyChannel channel;
	private AbstractPortSpecificKiSyChannel channel2;

	private void execute() {
		channel = initChannel(56789);
		channel2 = initChannel(56790);
	}

	/**
	 * Send ping.
	 */
	public void sendPing() {
		channel.send(KiSyMessageCreator.createPing(channel), channel2.localAddress());
	}

	/**
	 * Inits the channel.
	 *
	 * @param port
	 *          the port
	 * @return the default port specific ki sy channel
	 */
	protected AbstractPortSpecificKiSyChannel initChannel(int port) {
		AbstractPortSpecificKiSyChannel channel = new AbstractPortSpecificKiSyChannel(port) {

			@Override
			protected ChannelInitializer<DatagramChannel> initializer() {
				return new KiSyMessageInitializer();
			}

			@Override
			protected Class<? extends DatagramChannel> getChannelClass() {
				return NioDatagramChannel.class;
			}

		};

		channel.bind();

		return channel;
	}

	/**
	 * Disconnect.
	 *
	 * @throws InterruptedException
	 *           the interrupted exception
	 */
	public void disconnect() throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(2);
		ChannelFuture cf = channel.close();
		addLatchListener(cf, cdl);

		ChannelFuture cf2 = channel2.close();
		addLatchListener(cf2, cdl);

		cdl.await();
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *          the args
	 * @throws Exception
	 *           the exception
	 */
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
