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
package com.github.mrstampy.kitchensync.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;

// TODO: Auto-generated Javadoc
/**
 * The Interface KiSyMulticastChannel.
 */
public interface KiSyMulticastChannel extends KiSyChannel<DatagramChannel> {

	/**
	 * Broadcast.
	 *
	 * @param <MSG> the generic type
	 * @param message the message
	 * @return the channel future
	 */
	<MSG> ChannelFuture broadcast(MSG message);

	/**
	 * Join group.
	 *
	 * @return true, if join group
	 */
	boolean joinGroup();

	/**
	 * Leave group.
	 *
	 * @return true, if leave group
	 */
	boolean leaveGroup();

	/**
	 * Gets the multicast address.
	 *
	 * @return the multicast address
	 */
	InetSocketAddress getMulticastAddress();

	/**
	 * Gets the network interface.
	 *
	 * @return the network interface
	 */
	NetworkInterface getNetworkInterface();
}
