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
package com.github.mrstampy.kitchensync.message;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.mrstampy.kitchensync.message.inbound.ack.AckInboundMessageHandler;
import com.github.mrstampy.kitchensync.netty.channel.PacketCreator;
import com.github.mrstampy.kitchensync.netty.handler.KiSyMessageHandler;

/**
 * This class exists as a demonstration of what is possible with KitchenSync,
 * however it can be used as-is for messaging. It is serialized to and from Json
 * - see {@link KiSyMessageHandler} and {@link PacketCreator} for details. The
 * {@link #getTypes()} define the type of message and the
 * {@link #addMessagePart(String, String)} method allows an arbitrary number of
 * key/value pairs to be included in the message.
 */
public class KiSyMessage implements Serializable {

	private static final long serialVersionUID = -5792377785617661966L;

	private KiSyMessageType[] types;
	private long createTime;
	private String originatingHost;
	private int originatingPort;
	private boolean ackRequired;
	private InetSocketAddress remoteAddress;

	private Map<String, String> messageParts = new HashMap<String, String>();

	/**
	 * Blank constructor for Json serialization. Use the other constructors to
	 * create KiSyMessages.
	 */
	public KiSyMessage() {
	}

	/**
	 * The Constructor.
	 *
	 * @param originator
	 *          the originator of the message
	 * @param types
	 *          the types applicable for this message
	 */
	public KiSyMessage(InetSocketAddress originator, KiSyMessageType... types) {
		this(originator.getAddress(), originator.getPort(), types);
	}

	/**
	 * The Constructor.
	 *
	 * @param address
	 *          the address the InetAddress of the originator
	 * @param port
	 *          the port on which this message is to be sent
	 * @param types
	 *          the types applicable for this message
	 */
	public KiSyMessage(InetAddress address, int port, KiSyMessageType... types) {
		setTypes(types);
		setCreateTime(System.currentTimeMillis());

		setOriginatingHost(address.getHostAddress());
		setOriginatingPort(port);
	}

	/**
	 * Gets the keys of the arbitrary key/value pairs associated with this
	 * message.
	 *
	 * @return the keys
	 */
	@JsonIgnore
	public Set<String> getKeys() {
		return messageParts.keySet();
	}

	/**
	 * Adds an arbitrary key/value pair to this message.
	 *
	 * @param messageKey
	 *          the message key
	 * @param message
	 *          the message value
	 */
	@JsonIgnore
	public void addMessagePart(String messageKey, String message) {
		messageParts.put(messageKey, message);
	}

	/**
	 * Gets the message part for the specified key.
	 *
	 * @param messageKey
	 *          the message key
	 * @return the message part
	 */
	@JsonIgnore
	public String getMessagePart(String messageKey) {
		return messageParts.get(messageKey);
	}

	/**
	 * Gets the types applicable for this message.
	 *
	 * @return the types
	 */
	public KiSyMessageType[] getTypes() {
		return types;
	}

	/**
	 * Sets the types applicable for this message.
	 *
	 * @param types
	 *          the types
	 */
	public void setTypes(KiSyMessageType... types) {
		this.types = types;
	}

	/**
	 * Returns true if all the candidate types specified match the
	 * {@link #getTypes()} of this message, false otherwise.
	 *
	 * @param candidates
	 *          the candidates
	 * @return true, if checks if is type
	 */
	public boolean isType(KiSyMessageType... candidates) {
		if (candidates == null || candidates.length == 0) return getTypes() == null || getTypes().length == 0;

		for (KiSyMessageType candidate : candidates) {
			if (isNotInTypes(candidate)) return false;
		}

		return true;
	}

	private boolean isNotInTypes(KiSyMessageType candidate) {
		for (KiSyMessageType type : getTypes()) {
			if (type == candidate) return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, "origins");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "origins");
	}

	/**
	 * Gets the create time of this message.
	 *
	 * @return the creates the time
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * Sets the create time of this message.
	 *
	 * @param createTime
	 *          the creates the time
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * Gets the message parts.
	 *
	 * @return the message parts
	 */
	public Map<String, String> getMessageParts() {
		return messageParts;
	}

	/**
	 * Sets the message parts.
	 *
	 * @param messageParts
	 *          the message parts
	 */
	public void setMessageParts(Map<String, String> messageParts) {
		this.messageParts = messageParts;
	}

	/**
	 * Gets the originating host.
	 *
	 * @return the originating host
	 */
	public String getOriginatingHost() {
		return originatingHost;
	}

	/**
	 * Sets the originating host.
	 *
	 * @param originatingHost
	 *          the originating host
	 */
	public void setOriginatingHost(String originatingHost) {
		this.originatingHost = originatingHost;
	}

	/**
	 * Gets the originating port.
	 *
	 * @return the originating port
	 */
	public int getOriginatingPort() {
		return originatingPort;
	}

	/**
	 * Sets the originating port.
	 *
	 * @param originatingPort
	 *          the originating port
	 */
	public void setOriginatingPort(int originatingPort) {
		this.originatingPort = originatingPort;
	}

	/**
	 * Creates the return address from the originating host and port. Note that on
	 * message receipt the {@link #setRemoteAddress(InetSocketAddress)} is called;
	 * this address should be used in preference for direct connections.
	 *
	 * @return the inet socket address
	 */
	public InetSocketAddress createReturnAddress() {
		return new InetSocketAddress(getOriginatingHost(), getOriginatingPort());
	}

	/**
	 * Checks if an acknowledgement of message receipt is required.
	 *
	 * @return true, if checks if is ack required
	 * @see KiSyMessageCreator#requireAcknowledgement(KiSyMessage)
	 * @see AckInboundMessageHandler
	 */
	public boolean isAckRequired() {
		return ackRequired;
	}

	/**
	 * Sets if an acknowledgement of message receipt is required.
	 *
	 * @param ackRequired
	 *          the ack required
	 * @see KiSyMessageCreator#requireAcknowledgement(KiSyMessage)
	 * @see AckInboundMessageHandler
	 */
	public void setAckRequired(boolean ackRequired) {
		this.ackRequired = ackRequired;
	}

	/**
	 * Gets the remote address. This value is set on the receipt of a KiSyMessage.
	 *
	 * @return the remote address
	 */
	@JsonIgnore
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * Sets the remote address.
	 *
	 * @param remoteAddress
	 *          the remote address
	 */
	public void setRemoteAddress(InetSocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	/**
	 * Convenience method which returns the {@link #getRemoteAddress()} if set,
	 * otherwise returns the {@link #createReturnAddress()}.
	 *
	 * @return the return address
	 */
	@JsonIgnore
	public InetSocketAddress getReturnAddress() {
		return getRemoteAddress() == null ? createReturnAddress() : getRemoteAddress();
	}

}
