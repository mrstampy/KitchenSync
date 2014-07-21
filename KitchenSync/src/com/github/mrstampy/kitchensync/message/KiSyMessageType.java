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

/**
 * Various message types used to define a {@link KiSyMessage}'s purpose.
 */
public enum KiSyMessageType {

	//@formatter:off
	/** The ping. */
	PING,
	
	/** The pong. */
	PONG,
	
	/** The ack. */
	ACK,
	
	/** The ping time. */
	PING_TIME,
	
	/** The network state. */
	NETWORK_STATE,
	
	/** The register. */
	REGISTER,
	
	/** The registered. */
	REGISTERED,
	
	/** The connected. */
	CONNECTED,
	
	/** The disconnected. */
	DISCONNECTED,
	
	/** The trace. */
	TRACE,
	
	/** The info. */
	INFO,
	
	/** The urgent. */
	URGENT,
	
	/** The critical. */
	CRITICAL,
	
	/** The priviledged. */
	PRIVILEDGED,
	
	/** The media image. */
	MEDIA_IMAGE,
	
	/** The media sound. */
	MEDIA_SOUND,
	
	/** The media video. */
	MEDIA_VIDEO,
	
	/** The custom. */
	CUSTOM;
	//@formatter:on
}
