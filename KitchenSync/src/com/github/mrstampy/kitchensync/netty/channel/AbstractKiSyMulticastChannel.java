package com.github.mrstampy.kitchensync.netty.channel;

import io.netty.channel.ChannelFuture;

import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

/**
 * private static final String MULTICAST_IP = "FF05:0:0548:c4e6:796c:0:de66:FC";
 * private static final int MULTICAST_PORT = 57962;
 * 
 * @author burton
 *
 */
public abstract class AbstractKiSyMulticastChannel extends AbstractKiSyChannel implements KiSyMulticastChannel {

	private InetSocketAddress multicastAddress;
	private NetworkInterface networkInterface;

	public AbstractKiSyMulticastChannel(String multicastIPv6, int port) throws UnknownHostException {
		this(multicastIPv6, port, null);
	}

	public AbstractKiSyMulticastChannel(String multicastIPv6, int port, NetworkInterface networkInterface)
			throws UnknownHostException {
		this(new InetSocketAddress(Inet6Address.getByName(multicastIPv6), port), networkInterface);
	}

	public AbstractKiSyMulticastChannel(InetSocketAddress multicastAddress) {
		this(multicastAddress, null);
	}

	public AbstractKiSyMulticastChannel(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
		super();
		this.multicastAddress = multicastAddress;
		this.networkInterface = networkInterface == null ? bootstrapper.DEFAULT_INTERFACE : networkInterface;
	}

	@Override
	public void multicastBind() {
		if (isActive()) closeChannel();

		if (!bootstrapper.containsMulticastBootstrap(getMulticastAddress())) {
			bootstrapper.initMulticastBootstrap(initializer(), getMulticastAddress(), getNetworkInterface());
		}

		setChannel(bootstrapper.multicastBind(getMulticastAddress()));
	}

	@Override
	public <MSG extends Object> ChannelFuture broadcast(MSG message) {
		return sendImpl(createMessage(message, getMulticastAddress()), getMulticastAddress());
	}

	@Override
	public boolean joinGroup() {
		if (!isActive()) return false;

		return bootstrapper.joinGroup(getChannel());
	}

	@Override
	public boolean leaveGroup() {
		if (!isActive()) return false;

		return bootstrapper.leaveGroup(getChannel());
	}

	public InetSocketAddress getMulticastAddress() {
		return multicastAddress;
	}

	public NetworkInterface getNetworkInterface() {
		return networkInterface;
	}

	public int getPort() {
		return getMulticastAddress().getPort();
	}

	public String createMulticastKey() {
		return createMulticastKey(getMulticastAddress());
	}

	public static String createMulticastKey(InetSocketAddress address) {
		return new String(address.getAddress().getAddress());
	}

}
