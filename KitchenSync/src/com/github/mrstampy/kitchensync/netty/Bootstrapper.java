package com.github.mrstampy.kitchensync.netty;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyMulticastChannel;

public class Bootstrapper {
	private static final Logger log = LoggerFactory.getLogger(Bootstrapper.class);

	private static final int DEFAULT_BOOTSTRAP_KEY = -1;
	private static final AttributeKey<NetworkInterface> NI_KEY = AttributeKey.valueOf("KitchenSync Network Interface");
	private static final AttributeKey<InetSocketAddress> ISA_KEY = AttributeKey.valueOf("KitchenSync Multicast Address");

	public static NetworkInterface DEFAULT_INTERFACE;
	public static InetAddress DEFAULT_ADDRESS;
	static {
		try {
			DEFAULT_ADDRESS = InetAddress.getLocalHost();
			DEFAULT_INTERFACE = NetworkInterface.getByInetAddress(DEFAULT_ADDRESS);
		} catch (Exception e) {
			log.error("Could not determine network interface", e);
			throw new RuntimeException(e);
		}
	}

	private static final Bootstrapper INSTANCE = new Bootstrapper();

	public static Bootstrapper getInstance() {
		return INSTANCE;
	}

	private Map<Integer, Bootstrap> channelBootstraps = new ConcurrentHashMap<Integer, Bootstrap>();
	private Map<String, Bootstrap> multicastBootstraps = new ConcurrentHashMap<String, Bootstrap>();

	protected Bootstrapper() {
		// singleton
	}

	public boolean hasDefaultBootstrap() {
		return containsBootstrap(DEFAULT_BOOTSTRAP_KEY);
	}

	public boolean containsBootstrap(int port) {
		return channelBootstraps.containsKey(port);
	}

	public boolean containsMulticastBootstrap(InetSocketAddress multicast) {
		String key = createMulticastKey(multicast);

		return containsMulticastBootstrap(key);
	}

	public <CHANNEL extends DatagramChannel> void initDefaultBootstrap(ChannelInitializer<CHANNEL> initializer,
			Class<? extends CHANNEL> clazz) {
		initBootstrap(initializer, DEFAULT_BOOTSTRAP_KEY, clazz);
	}

	public <CHANNEL extends DatagramChannel> void initBootstrap(ChannelInitializer<CHANNEL> initializer, int port,
			Class<? extends CHANNEL> clazz) {
		if (containsBootstrap(port)) {
			log.warn("Bootstrap for port {} already initialized", port);
			return;
		}

		log.debug("Initializing bootstrap for port {}", port);
		Bootstrap b = bootstrap(initializer, port, clazz);

		channelBootstraps.put(port, b);
	}

	public void initMulticastBootstrap(ChannelInitializer<DatagramChannel> initializer, InetSocketAddress multicast) {
		initMulticastBootstrap(initializer, multicast, DEFAULT_INTERFACE);
	}

	public void initMulticastBootstrap(ChannelInitializer<DatagramChannel> initializer, InetSocketAddress multicast,
			NetworkInterface networkInterface) {
		String key = createMulticastKey(multicast);
		if (containsMulticastBootstrap(key)) {
			log.warn("Multicast bootstrap for {} already initialized", multicast);
			return;
		}

		log.debug("Initializing multicast bootstrap for {} using network interface {}", multicast, networkInterface);

		Bootstrap b = multicastBootstrap(initializer, multicast, networkInterface);

		multicastBootstraps.put(key, b);
	}

	private String createMulticastKey(InetSocketAddress multicast) {
		return AbstractKiSyMulticastChannel.createMulticastKey(multicast);
	}

	public Bootstrap removeBootstrap(int port) {
		return channelBootstraps.remove(port);
	}

	public Bootstrap removeMulticastBootstrap(InetSocketAddress address) {
		return removeMulticastBootstrap(createMulticastKey(address));
	}

	public Bootstrap removeMulticastBootstrap(String key) {
		return multicastBootstraps.remove(key);
	}

	public Set<Integer> getBootstrapKeys() {
		return channelBootstraps.keySet();
	}

	public Set<String> getMulticastBootstrapKeys() {
		return multicastBootstraps.keySet();
	}

	public void clearBootstraps() {
		channelBootstraps.clear();
	}

	public void clearMulticastBootstraps() {
		multicastBootstraps.clear();
	}

	public <CHANNEL extends DatagramChannel> CHANNEL bind() {
		return bind(0);
	}

	@SuppressWarnings("unchecked")
	public <CHANNEL extends DatagramChannel> CHANNEL bind(int port) {
		boolean contains = containsBootstrap(port);
		if (!contains && !hasDefaultBootstrap()) {
			log.error("Bootstrap for port {} not initialized", port);
			return null;
		}

		Bootstrap b = channelBootstraps.get(contains ? port : DEFAULT_BOOTSTRAP_KEY);

		ChannelFuture cf = b.bind(port);

		CountDownLatch latch = new CountDownLatch(1);
		cf.addListener(getBindListener(port, latch));

		await(latch, "Channel creation timed out");

		return cf.isSuccess() ? (CHANNEL) cf.channel() : null;
	}

	public DatagramChannel multicastBind(InetSocketAddress multicast) {
		String key = createMulticastKey(multicast);
		if (!containsMulticastBootstrap(key)) {
			log.error("Multicast bootstrap for {} not initialized", multicast);
			return null;
		}

		Bootstrap b = multicastBootstraps.get(key);

		ChannelFuture cf = b.bind();

		CountDownLatch latch = new CountDownLatch(1);
		cf.addListener(getMulticastBindListener(multicast, latch));

		await(latch, "Multicast channel creation timed out");

		return cf.isSuccess() ? (DatagramChannel) cf.channel() : null;
	}

	public boolean joinGroup(DatagramChannel channel) {
		if (!isMulticastChannel(channel)) {
			log.error("Not a multicast channel, cannot join group");
			return false;
		}

		InetSocketAddress multicast = getMulticastAddress(channel);
		NetworkInterface ni = getNetworkInterface(channel);

		ChannelFuture cf = channel.joinGroup(multicast, ni);

		CountDownLatch latch = new CountDownLatch(1);
		cf.addListener(getJoinGroupListener(multicast, latch));

		await(latch, "Multicast channel join group timed out");

		return cf.isSuccess();
	}

	public boolean leaveGroup(DatagramChannel channel) {
		if (!isMulticastChannel(channel)) {
			log.error("Not a multicast channel, cannot leave group");
			return false;
		}

		InetSocketAddress multicast = getMulticastAddress(channel);
		NetworkInterface ni = getNetworkInterface(channel);

		ChannelFuture cf = channel.leaveGroup(multicast, ni);

		CountDownLatch latch = new CountDownLatch(1);
		cf.addListener(getLeaveGroupListener(multicast, latch));

		await(latch, "Multicast channel leave group timed out");

		return cf.isSuccess();
	}

	public InetSocketAddress getMulticastAddress(Channel channel) {
		return channel == null ? null : channel.attr(ISA_KEY).get();
	}

	public NetworkInterface getNetworkInterface(Channel channel) {
		return channel == null ? null : channel.attr(NI_KEY).get();
	}

	public boolean isMulticastChannel(Channel channel) {
		return getMulticastAddress(channel) != null && getNetworkInterface(channel) != null;
	}

	private GenericFutureListener<ChannelFuture> getBindListener(final int port, final CountDownLatch latch) {
		return new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				try {
					if (future.isSuccess()) {
						log.debug("Channel creation successful for {}", future.channel());
					} else {
						Throwable cause = future.cause();
						if (cause == null) {
							log.error("Could not create channel for {}", port);
						} else {
							log.error("Could not create channel for {}", port, cause);
						}
					}
				} finally {
					latch.countDown();
				}
			}
		};
	}

	private GenericFutureListener<ChannelFuture> getMulticastBindListener(final InetSocketAddress multicast,
			final CountDownLatch latch) {
		return new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				try {
					if (future.isSuccess()) {
						log.debug("Multicast channel creation successful for {}", multicast);
					} else {
						Throwable cause = future.cause();
						if (cause == null) {
							log.error("Could not create multicast channel for {}", multicast);
						} else {
							log.error("Could not create multicast channel for {}", multicast, cause);
						}
					}
				} finally {
					latch.countDown();
				}
			}
		};
	}

	private GenericFutureListener<ChannelFuture> getJoinGroupListener(final InetSocketAddress multicast,
			final CountDownLatch latch) {
		return new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				try {
					if (future.isSuccess()) {
						log.debug("Multicast channel joined group {}", multicast);
					} else {
						Throwable cause = future.cause();
						if (cause == null) {
							log.error("Could not join multicast group for {}", multicast);
						} else {
							log.error("Could not join multicast group for {}", multicast, cause);
						}
					}
				} finally {
					latch.countDown();
				}
			}
		};
	}

	private GenericFutureListener<ChannelFuture> getLeaveGroupListener(final InetSocketAddress multicast,
			final CountDownLatch latch) {
		return new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				try {
					if (future.isSuccess()) {
						log.debug("Multicast channel left group {}", multicast);
					} else {
						Throwable cause = future.cause();
						if (cause == null) {
							log.error("Could not leave multicast group for {}", multicast);
						} else {
							log.error("Could not leave multicast group for {}", multicast, cause);
						}
					}
				} finally {
					latch.countDown();
				}
			}
		};
	}

	private void await(CountDownLatch latch, String error) {
		try {
			boolean ok = latch.await(5, TimeUnit.SECONDS);
			if (!ok) log.error(error);
		} catch (InterruptedException e) {
			// ignored
		}
	}

	protected <CHANNEL extends DatagramChannel> Bootstrap bootstrap(ChannelInitializer<CHANNEL> initializer, int port,
			Class<? extends CHANNEL> clazz) {
		Bootstrap b = new Bootstrap();

		setDefaultBootstrapOptions(b, initializer);
		if (port > 0) b.localAddress(port);

		b.group(getEventLoopGroup(clazz));
		b.channel(clazz);

		return b;
	}

	protected <CHANNEL extends DatagramChannel> EventLoopGroup getEventLoopGroup(Class<? extends CHANNEL> clazz) {
		switch (clazz.getSimpleName()) {
		case "NioDatagramChannel":
			return new NioEventLoopGroup();
		case "OioDatagramChannel":
			return new OioEventLoopGroup();
		default:
			throw new UnsupportedOperationException("No default event loop group defined for " + clazz.getName());
		}
	}

	protected void setDefaultBootstrapOptions(AbstractBootstrap<?, ?> b, ChannelInitializer<?> initializer) {
		b.option(ChannelOption.SO_BROADCAST, true);
		b.option(ChannelOption.SO_REUSEADDR, true);
		b.handler(initializer);
	}

	protected Bootstrap multicastBootstrap(ChannelInitializer<DatagramChannel> initializer, InetSocketAddress multicast) {
		return multicastBootstrap(initializer, multicast, DEFAULT_INTERFACE);
	}

	protected Bootstrap multicastBootstrap(ChannelInitializer<DatagramChannel> initializer, InetSocketAddress multicast,
			NetworkInterface networkInterface) {
		Bootstrap b = bootstrap(initializer, multicast.getPort(), NioDatagramChannel.class);

		b.option(ChannelOption.IP_MULTICAST_IF, networkInterface);

		b.attr(NI_KEY, networkInterface);
		b.attr(ISA_KEY, multicast);

		return b;
	}

	private boolean containsMulticastBootstrap(String key) {
		return multicastBootstraps.containsKey(key);
	}
}
