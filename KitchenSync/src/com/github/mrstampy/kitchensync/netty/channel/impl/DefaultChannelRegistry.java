package com.github.mrstampy.kitchensync.netty.channel.impl;

import io.netty.channel.socket.DatagramChannel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.netty.channel.AbstractKiSyChannel;

public class DefaultChannelRegistry {

	private Map<Integer, AbstractKiSyChannel<DatagramChannel, KiSyMessage>> channels = new ConcurrentHashMap<Integer, AbstractKiSyChannel<DatagramChannel, KiSyMessage>>();
	private Map<String, DefaultKiSyMulticastChannel> multicastChannels = new ConcurrentHashMap<String, DefaultKiSyMulticastChannel>();

	public static final DefaultChannelRegistry INSTANCE = new DefaultChannelRegistry();

	protected DefaultChannelRegistry() {
	}
	
	public AbstractKiSyChannel<DatagramChannel, KiSyMessage> getChannel(int port) {
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

	public void addChannel(AbstractKiSyChannel<DatagramChannel, KiSyMessage> channel) {
		if (!channels.containsKey(channel.getPort())) channels.put(channel.getPort(), channel);
	}

	public void removeChannel(AbstractKiSyChannel<DatagramChannel, KiSyMessage> channel) {
		removeChannel(channel.getPort());
	}

	public AbstractKiSyChannel<DatagramChannel, KiSyMessage> removeChannel(int port) {
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
