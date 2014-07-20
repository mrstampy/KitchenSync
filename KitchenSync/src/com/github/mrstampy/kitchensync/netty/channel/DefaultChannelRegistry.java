package com.github.mrstampy.kitchensync.netty.channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultKiSyMulticastChannel;

public class DefaultChannelRegistry {

	private Map<Integer, AbstractKiSyChannel> channels = new ConcurrentHashMap<Integer, AbstractKiSyChannel>();
	private Map<String, DefaultKiSyMulticastChannel> multicastChannels = new ConcurrentHashMap<String, DefaultKiSyMulticastChannel>();

	public static final DefaultChannelRegistry INSTANCE = new DefaultChannelRegistry();

	protected DefaultChannelRegistry() {
	}

	public AbstractKiSyChannel getChannel(int port) {
		return channels.get(port);
	}

	public DefaultKiSyMulticastChannel getMulticastChannel(InetSocketAddress address) {
		return getMulticastChannel(DefaultKiSyMulticastChannel.createMulticastKey(address));
	}

	public DefaultKiSyMulticastChannel getMulticastChannel(String key) {
		return multicastChannels.get(key);
	}

	public Set<Integer> getChannelKeys() {
		return channels.keySet();
	}

	public Set<String> getMulticastChannelKeys() {
		return multicastChannels.keySet();
	}

	public boolean isMulticastChannel(InetSocketAddress address) {
		return multicastChannels.containsKey(DefaultKiSyMulticastChannel.createMulticastKey(address));
	}

	public void addChannel(AbstractKiSyChannel channel) {
		if (!channels.containsKey(channel.getPort())) channels.put(channel.getPort(), channel);
	}

	public void removeChannel(AbstractKiSyChannel channel) {
		removeChannel(channel.getPort());
	}

	public AbstractKiSyChannel removeChannel(int port) {
		return channels.remove(port);
	}

	public void clearChannels() {
		channels.clear();
	}

	public void addMulticastChannel(DefaultKiSyMulticastChannel channel) {
		String key = channel.createMulticastKey();
		if (!multicastChannels.containsKey(key)) multicastChannels.put(key, channel);
	}

	public void removeMulticastChannel(DefaultKiSyMulticastChannel channel) {
		removeMulticastChannel(channel.createMulticastKey());
	}

	public DefaultKiSyMulticastChannel removeMulticastChannel(String key) {
		return multicastChannels.remove(key);
	}

	public void clearMulticastChannels() {
		multicastChannels.clear();
	}
}
