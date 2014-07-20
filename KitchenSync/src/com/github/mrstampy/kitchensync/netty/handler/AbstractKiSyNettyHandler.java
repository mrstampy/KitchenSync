package com.github.mrstampy.kitchensync.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultChannelRegistry;

public abstract class AbstractKiSyNettyHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private static final Logger log = LoggerFactory.getLogger(AbstractKiSyNettyHandler.class);

	private KiSyInboundMessageManager<KiSyMessage> handlerManager = KiSyInboundMessageManager.INSTANCE;
	private DefaultChannelRegistry registry = DefaultChannelRegistry.INSTANCE;

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Unexpected exception", cause);
	}

	protected KiSyChannel<DatagramChannel> getChannel(DatagramPacket packet) {
		InetSocketAddress recipient = packet.recipient();
		return isMulticastChannel(recipient) ? getMulticastChannel(recipient) : getChannel(recipient.getPort());
	}

	protected boolean isMulticastChannel(InetSocketAddress local) {
		return registry.isMulticastChannel(local);
	}

	protected KiSyChannel<DatagramChannel> getMulticastChannel(InetSocketAddress local) {
		return registry.getMulticastChannel(local);
	}

	protected KiSyChannel<DatagramChannel> getChannel(int port) {
		return registry.getChannel(port);
	}

	protected void processMessage(KiSyMessage message, DatagramPacket msg) {
		message.setRemoteAddress(msg.sender());
		handlerManager.processMessage(message, getChannel(msg));
	}

	protected String content(DatagramPacket msg) {
		return msg.content().toString(CharsetUtil.UTF_8);
	}
}
