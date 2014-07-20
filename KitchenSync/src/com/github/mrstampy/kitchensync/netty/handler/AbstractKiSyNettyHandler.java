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

public abstract class AbstractKiSyNettyHandler<MSG> extends SimpleChannelInboundHandler<DatagramPacket> {
	private static final Logger log = LoggerFactory.getLogger(AbstractKiSyNettyHandler.class);

	private KiSyMessageInboundMessageManager kiSyMessageHandler = KiSyMessageInboundMessageManager.INSTANCE;
	private ByteArrayInboundMessageManager byteArrayMessageHandler = ByteArrayInboundMessageManager.INSTANCE;
	private StringInboundMessageManager stringMessageHandler = StringInboundMessageManager.INSTANCE;
	private KiSyInboundMessageManager<MSG> customHandler;

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

	protected void processCustom(MSG message, DatagramPacket msg) {
		if (getCustomHandler() == null) {
			log.error("No custom handler set");
			return;
		}

		customHandler.processMessage(message, getChannel(msg));
	}

	protected void processString(String message, DatagramPacket msg) {
		stringMessageHandler.processMessage(message, getChannel(msg));
	}

	protected void processKiSyMessage(KiSyMessage message, DatagramPacket msg) {
		KiSyMessage ksm = (KiSyMessage) message;
		ksm.setRemoteAddress(msg.sender());
		kiSyMessageHandler.processMessage(ksm, getChannel(msg));
	}

	protected void processBytes(byte[] message, DatagramPacket msg) {
		byteArrayMessageHandler.processMessage(message, getChannel(msg));
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

	public KiSyInboundMessageManager<MSG> getCustomHandler() {
		return customHandler;
	}

	public void setCustomHandler(KiSyInboundMessageManager<MSG> customHandler) {
		this.customHandler = customHandler;
	}

}
