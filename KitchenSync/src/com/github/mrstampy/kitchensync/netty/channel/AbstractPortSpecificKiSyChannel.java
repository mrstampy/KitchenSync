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

import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;
import com.github.mrstampy.kitchensync.message.outbound.KiSyOutboundMessageManager;

import io.netty.channel.socket.DatagramChannel;

/**
 * Superclass for port-specific channels with their own Bootstrap. After
 * initializing the {@link KiSyInboundMessageManager} and
 * {@link KiSyOutboundMessageManager} with appropriate handlers the use of this
 * class is as so:<p>
 * 
 * <pre>
 * {@code
 *	protected KiSyChannel initChannel(int port) {
 *		AbstractPortSpecificKiSyChannel channel = new AbstractPortSpecificKiSyChannel(port) {
 *
 *			protected ChannelInitializer<DatagramChannel> initializer() {
 *				return new MyOwnChannelInitializer();
 *			}
 *
 *			protected Class<? extends DatagramChannel> getChannelClass() {
 *				return NioDatagramChannel.class;
 *			}
 *
 *		};
 *
 *		channel.bind();
 *
 *		return channel;
 *	}
 * }
 * </pre>
 */
public abstract class AbstractPortSpecificKiSyChannel extends AbstractKiSyChannel {

	private int port;

	/**
	 * The Constructor.
	 *
	 * @param port
	 *          the port
	 */
	public AbstractPortSpecificKiSyChannel(int port) {
		super();
		this.port = port;
	}

	/**
	 * Binds to the port specified in the constructor.
	 */
	public void bind() {
		bindBootstrapInit(getPort());

		DatagramChannel channel = bootstrapper.bind(getPort());

		setChannel(channel);

		registry.addChannel(this);
	}

	/**
	 * Ignores the specified port and binds to the port specified in the
	 * constructor.
	 */
	@Override
	public void bind(int port) {
		bind();
	}

	/**
	 * Bind bootstrap init.
	 *
	 * @param port
	 *          the port
	 */
	protected void bindBootstrapInit(int port) {
		if (isActive()) closeChannel();
		if (!bootstrapper.containsBootstrap(port)) bootstrapper.initBootstrap(initializer(), port, getChannelClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyChannel#getPort()
	 */
	@Override
	public int getPort() {
		return port;
	}

}
