package com.github.mrstampy.kitchensync.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KiSyMessage implements Serializable {

	private static final long serialVersionUID = -5792377785617661966L;

	private KiSyMessageType[] types;
	private long createTime;

	private Map<String, String> messageParts = new HashMap<String, String>();

	public KiSyMessage() {
	}

	public KiSyMessage(KiSyMessageType... types) {
		setTypes(types);
		setCreateTime(System.currentTimeMillis());
	}

	@JsonIgnore
	public Set<String> getKeys() {
		return messageParts.keySet();
	}

	@JsonIgnore
	public void addMessage(String messageKey, String message) {
		messageParts.put(messageKey, message);
	}

	@JsonIgnore
	public String getMessagePart(String messageKey) {
		return messageParts.get(messageKey);
	}

	public KiSyMessageType[] getTypes() {
		return types;
	}

	public void setTypes(KiSyMessageType... types) {
		this.types = types;
	}

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

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, "origins");
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "origins");
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public Map<String, String> getMessageParts() {
		return messageParts;
	}

	public void setMessageParts(Map<String, String> messageParts) {
		this.messageParts = messageParts;
	}

}
