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
package com.github.mrstampy.kitchensync.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.inbound.ByteArrayInboundMessageManager;
import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;
import com.github.mrstampy.kitchensync.message.inbound.KiSyMessageInboundMessageManager;
import com.github.mrstampy.kitchensync.message.inbound.StringInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.DefaultChannelRegistry;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

/**
 * Abstract superclass providing the ability to handle string, byte array and
 * {@link KiSyMessage}s and providing the ability to specify a custom
 * {@link KiSyInboundMessageManager}.
 *
 * @param <MSG>
 *          the generic type
 */
public abstract class AbstractKiSyNettyHandler<MSG> extends SimpleChannelInboundHandler<DatagramPacket> {
	private static final Logger log = LoggerFactory.getLogger(AbstractKiSyNettyHandler.class);

	private KiSyMessageInboundMessageManager kiSyMessageHandler = KiSyMessageInboundMessageManager.INSTANCE;
	private ByteArrayInboundMessageManager byteArrayMessageHandler = ByteArrayInboundMessageManager.INSTANCE;
	private StringInboundMessageManager stringMessageHandler = StringInboundMessageManager.INSTANCE;
	private KiSyInboundMessageManager<MSG> customHandler;

	private DefaultChannelRegistry registry = DefaultChannelRegistry.INSTANCE;

	private HandlerType type;

	/**
	 * The Constructor. Should the type be {@link HandlerType#CUSTOM} then the
	 * {@link #setCustomHandler(KiSyInboundMessageManager)} must also be invoked.
	 *
	 * @param type
	 *          the type
	 */
	protected AbstractKiSyNettyHandler(HandlerType type) {
		this.type = type;
	}

	/**
	 * Specify a custom inbound message manager for this handler
	 * 
	 * @param custom
	 */
	protected AbstractKiSyNettyHandler(KiSyInboundMessageManager<MSG> custom) {
		this(HandlerType.CUSTOM);
		setCustomHandler(custom);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.
	 * channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Unexpected exception", cause);
	}

	/**
	 * Gets the channel.
	 *
	 * @param packet
	 *          the packet
	 * @return the channel
	 */
	protected KiSyChannel<DatagramChannel> getChannel(DatagramPacket packet) {
		InetSocketAddress recipient = packet.recipient();
		return isMulticastChannel(recipient) ? getMulticastChannel(recipient) : getChannel(recipient.getPort());
	}

	/**
	 * Checks if is multicast channel.
	 *
	 * @param local
	 *          the local
	 * @return true, if checks if is multicast channel
	 */
	protected boolean isMulticastChannel(InetSocketAddress local) {
		return registry.isMulticastChannel(local);
	}

	/**
	 * Gets the multicast channel.
	 *
	 * @param local
	 *          the local
	 * @return the multicast channel
	 */
	protected KiSyChannel<DatagramChannel> getMulticastChannel(InetSocketAddress local) {
		return registry.getMulticastChannel(local);
	}

	/**
	 * Gets the channel.
	 *
	 * @param port
	 *          the port
	 * @return the channel
	 */
	protected KiSyChannel<DatagramChannel> getChannel(int port) {
		return registry.getChannel(port);
	}

	/**
	 * Process message.
	 *
	 * @param message
	 *          the message
	 * @param msg
	 *          the msg
	 */
	protected void processMessage(MSG message, DatagramPacket msg) {
		switch (type) {
		case BYTE_ARRAY:
			processBytes((byte[]) message, msg);
			break;
		case KISY_MESSAGE:
			processKiSyMessage((KiSyMessage) message, msg);
			break;
		case STRING:
			processString((String) message, msg);
			break;
		case CUSTOM:
			processCustom(message, msg);
			break;
		default:
			log.error("Cannot process message of type {}", message.getClass());
			break;
		}
	}

	/**
	 * Process custom.
	 *
	 * @param message
	 *          the message
	 * @param msg
	 *          the msg
	 */
	protected void processCustom(MSG message, DatagramPacket msg) {
		if (getCustomHandler() == null) {
			log.error("No custom handler set");
			return;
		}

		customHandler.processMessage(message, getChannel(msg));
	}

	/**
	 * Process string.
	 *
	 * @param message
	 *          the message
	 * @param msg
	 *          the msg
	 */
	protected void processString(String message, DatagramPacket msg) {
		stringMessageHandler.processMessage(message, getChannel(msg));
	}

	/**
	 * Process ki sy message.
	 *
	 * @param message
	 *          the message
	 * @param msg
	 *          the msg
	 */
	protected void processKiSyMessage(KiSyMessage message, DatagramPacket msg) {
		KiSyMessage ksm = (KiSyMessage) message;
		ksm.setRemoteAddress(msg.sender());
		kiSyMessageHandler.processMessage(ksm, getChannel(msg));
	}

	/**
	 * Process bytes.
	 *
	 * @param message
	 *          the message
	 * @param msg
	 *          the msg
	 */
	protected void processBytes(byte[] message, DatagramPacket msg) {
		byteArrayMessageHandler.processMessage(message, getChannel(msg));
	}

	/**
	 * Content.
	 *
	 * @param msg
	 *          the msg
	 * @return the string
	 */
	protected String content(DatagramPacket msg) {
		return msg.content().toString(CharsetUtil.UTF_8);
	}

	/**
	 * Bytes.
	 *
	 * @param msg
	 *          the msg
	 * @return the byte[]
	 */
	protected byte[] bytes(DatagramPacket msg) {
		ByteBuf content = msg.content();

		int num = content.readableBytes();
		byte[] message = new byte[num];
		content.readBytes(message);

		return message;
	}

	/**
	 * Gets the custom handler.
	 *
	 * @return the custom handler
	 */
	public KiSyInboundMessageManager<MSG> getCustomHandler() {
		return customHandler;
	}

	/*
	 * Sets the custom handler.
	 *
	 * @param customHandler
	 *          the custom handler
	 */
	private void setCustomHandler(KiSyInboundMessageManager<MSG> customHandler) {
		this.customHandler = customHandler;
	}

}
