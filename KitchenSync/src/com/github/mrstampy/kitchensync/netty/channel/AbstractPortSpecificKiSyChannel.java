package com.github.mrstampy.kitchensync.netty.channel;

import io.netty.channel.socket.DatagramChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.Bootstrapper;

public abstract class AbstractPortSpecificKiSyChannel<CHANNEL extends DatagramChannel, MSG> extends
		AbstractKiSyChannel<CHANNEL, MSG> {
	private static final Logger log = LoggerFactory.getLogger(AbstractPortSpecificKiSyChannel.class);

	private int port;

	public AbstractPortSpecificKiSyChannel(int port) {
		this(port, Bootstrapper.getInstance());
	}

	public AbstractPortSpecificKiSyChannel(int port, Bootstrapper bootstrapper) {
		super(bootstrapper);
		this.port = port;
	}

	public void bind() {
		bindBootstrapInit(getPort());

		CHANNEL channel = bootstrapper.bind(getPort());

		setChannel(channel);
	}

	@Override
	public void bind(int port) {
		log.debug("Ignoring argument {}, using {} to bind", port, getPort());
		bind();
	}

	protected void bindBootstrapInit(int port) {
		if (isActive()) closeChannel();
		if (!bootstrapper.containsBootstrap(port)) bootstrapper.initBootstrap(initializer(), port, getChannelClass());
	}

	@Override
	public int getPort() {
		return port;
	}

}
