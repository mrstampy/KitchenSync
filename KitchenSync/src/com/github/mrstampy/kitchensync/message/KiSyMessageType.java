package com.github.mrstampy.kitchensync.message;

public enum KiSyMessageType {

	//@formatter:off
	PING,
	PONG,
	ACK,
	PING_TIME,
	NETWORK_STATE,
	REGISTER,
	REGISTERED,
	CONNECTED,
	DISCONNECTED,
	TRACE,
	INFO,
	URGENT,
	CRITICAL,
	PRIVILEDGED,
	MEDIA_IMAGE,
	MEDIA_SOUND,
	MEDIA_VIDEO,
	CUSTOM;
	//@formatter:on
}
