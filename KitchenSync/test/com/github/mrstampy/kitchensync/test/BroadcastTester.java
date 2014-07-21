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
import io.netty.util.concurrent.GenericFutureListener;

import java.net.UnknownHostException;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyMulticastChannel;
import com.github.mrstampy.kitchensync.netty.channel.initializer.KiSyMessageInitializer;

// TODO: Auto-generated Javadoc
/**
 * The Class BroadcastTester.
 */
public class BroadcastTester extends AbstractTester {
	private static final String MULTICAST_IP = "FF05:0:0548:c4e6:796c:0:de66:FC";
	private static final int MULTICAST_PORT = 57962;

	private AbstractKiSyMulticastChannel michael;
	private AbstractKiSyMulticastChannel robert;
	private AbstractKiSyMulticastChannel paul;

	private void init() throws UnknownHostException {
		michael = createMulticastChannel();
		robert = createMulticastChannel();
		paul = createMulticastChannel();
	}

	/**
	 * Execute.
	 */
	public void execute() {
		KiSyMessage message = new KiSyMessage(michael.getMulticastAddress(), KiSyMessageType.INFO);
		message.addMessage("greeting", "A good day to you all!");

		michael.leaveGroup();

		ChannelFuture cf = michael.broadcast(message);

		cf.addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				michael.joinGroup();
			}
		});
	}

	/**
	 * Creates the multicast channel.
	 *
	 * @return the default ki sy multicast channel
	 * @throws UnknownHostException
	 *           the unknown host exception
	 */
	protected AbstractKiSyMulticastChannel createMulticastChannel() throws UnknownHostException {
		AbstractKiSyMulticastChannel channel = new AbstractKiSyMulticastChannel(MULTICAST_IP, MULTICAST_PORT) {

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
		channel.joinGroup();

		return channel;
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
		BroadcastTester tester = new BroadcastTester();

		tester.init();

		tester.execute();
	}

}
