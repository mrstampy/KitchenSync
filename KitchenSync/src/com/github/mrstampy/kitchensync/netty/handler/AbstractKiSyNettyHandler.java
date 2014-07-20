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
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.kitchensync.netty.channel.impl.ByteArrayInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultChannelRegistry;
import com.github.mrstampy.kitchensync.netty.channel.impl.KiSyMessageInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.impl.StringInboundMessageManager;

public abstract class AbstractKiSyNettyHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	private static final Logger log = LoggerFactory.getLogger(AbstractKiSyNettyHandler.class);

	private KiSyMessageInboundMessageManager kiSyMessageHandler = KiSyMessageInboundMessageManager.INSTANCE;
	private ByteArrayInboundMessageManager byteArrayMessageHandler = ByteArrayInboundMessageManager.INSTANCE;
	private StringInboundMessageManager stringMessageHandler = StringInboundMessageManager.INSTANCE;

	private DefaultChannelRegistry registry = DefaultChannelRegistry.INSTANCE;

	private HandlerType type;

	protected AbstractKiSyNettyHandler(HandlerType type) {
		this.type = type;
	}

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

	protected <MSG> void processMessage(MSG message, DatagramPacket msg) {
		switch (type) {
		case BYTE_ARRAY:
			byteArrayMessageHandler.processMessage((byte[]) message, getChannel(msg));
			break;
		case KISY_MESSAGE:
			KiSyMessage ksm = (KiSyMessage) message;
			ksm.setRemoteAddress(msg.sender());
			kiSyMessageHandler.processMessage(ksm, getChannel(msg));
			break;
		case STRING:
			stringMessageHandler.processMessage((String) message, getChannel(msg));
			break;
		default:
			log.error("Cannot process message of type {}", message.getClass());
			break;
		}
	}

	protected String content(DatagramPacket msg) {
		return msg.content().toString(CharsetUtil.UTF_8);
	}
	
	protected byte[] bytes(DatagramPacket msg) {
		ByteBuf content = msg.content();
		
		int num = content.readableBytes();
		byte[] message = new byte[num];
		content.readBytes(message);
		
		return message;
	}

}
