package com.github.mrstampy.kitchensync.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.Bootstrapper;

public abstract class AbstractKiSyChannel<CHANNEL extends DatagramChannel, MSG> implements KiSyChannel<CHANNEL, MSG> {
	private static final Logger log = LoggerFactory.getLogger(AbstractKiSyChannel.class);

	protected static GenericFutureListener<ChannelFuture> SEND_FUTURE = new GenericFutureListener<ChannelFuture>() {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				log.trace("Message successfully sent");
			} else {
				Throwable cause = future.cause();
				if (cause == null) {
					log.error("Could not send message");
				} else {
					log.error("Could not send message", cause);
				}
			}
		}
	};

	protected static GenericFutureListener<ChannelFuture> CONNECT_FUTURE = new GenericFutureListener<ChannelFuture>() {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				log.debug("Connection to {} successful", future.channel().remoteAddress());
			} else {
				Throwable cause = future.cause();
				if (cause == null) {
					log.error("Could not connect");
				} else {
					log.error("Could not connect", cause);
				}
			}
		}
	};

	protected static GenericFutureListener<ChannelFuture> CLOSE_FUTURE = new GenericFutureListener<ChannelFuture>() {

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				log.trace("Channel successfully closed");
			} else {
				Throwable cause = future.cause();
				if (cause == null) {
					log.error("Could not close channel");
				} else {
					log.error("Could not close channel", cause);
				}
			}
		}
	};

	private CHANNEL channel;

	protected Bootstrapper bootstrapper;

	protected abstract ChannelInitializer<CHANNEL> initializer();

	protected abstract Class<? extends CHANNEL> getChannelClass();

	public AbstractKiSyChannel() {
		this(Bootstrapper.getInstance());
	}

	public AbstractKiSyChannel(Bootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}

	@Override
	public boolean isActive() {
		return getChannel() != null && getChannel().isActive();
	}

	@Override
	public void bind() {
		bindDefaultBootstrapInit();

		CHANNEL channel = bootstrapper.bind();
		setChannel(channel);
	}

	@Override
	public void bind(int port) {
		bindDefaultBootstrapInit();

		CHANNEL channel = bootstrapper.bind(port);
		setChannel(channel);
	}

	protected abstract Object createMessage(MSG message, InetSocketAddress address);

	@Override
	public ChannelFuture send(MSG message, InetSocketAddress address) {
		presend(message, address);
		Object msg = createMessage(message, address);
		return sendImpl(msg, address);
	}

	protected abstract void presend(MSG message, InetSocketAddress address);

	protected ChannelFuture sendImpl(Object dp, InetSocketAddress address) {
		if (!isActive()) {
			log.error("Channel is not active, cannot send {}", dp);
			return new KiSyFailedFuture();
		}

		ChannelFuture cf = getChannel().writeAndFlush(dp);

		cf.addListener(SEND_FUTURE);

		return cf;
	}

	public int getPort() {
		return isActive() ? getChannel().localAddress().getPort() : -1;
	}

	@Override
	public ChannelFuture close() {
		if (!isActive()) return new KiSyFailedFuture();

		ChannelFuture cf = getChannel().close();
		cf.addListener(CLOSE_FUTURE);

		return cf;
	}

	@Override
	public CHANNEL getChannel() {
		return channel;
	}

	public InetSocketAddress localAddress() {
		return new InetSocketAddress(Bootstrapper.DEFAULT_ADDRESS, getPort());
	}

	protected void setChannel(CHANNEL channel) {
		this.channel = channel;
	}

	protected void bindDefaultBootstrapInit() {
		if (isActive()) closeChannel();
		if (!bootstrapper.hasDefaultBootstrap()) bootstrapper.initDefaultBootstrap(initializer(), getChannelClass());
	}

	protected void closeChannel() {
		ChannelFuture cf = close();
		final CountDownLatch latch = new CountDownLatch(1);
		cf.addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				latch.countDown();
			}
		});

		await(latch, "Channel close timed out");
	}

	protected void await(CountDownLatch latch, String error) {
		try {
			boolean ok = latch.await(5, TimeUnit.SECONDS);
			if (!ok) log.error(error);
		} catch (InterruptedException e) {
			log.error("Unexpected interruption for {}", error, e);
		}
	}

}
