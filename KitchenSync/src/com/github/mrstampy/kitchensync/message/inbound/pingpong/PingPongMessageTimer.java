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
package com.github.mrstampy.kitchensync.message.inbound.pingpong;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageCreator;
import com.github.mrstampy.kitchensync.message.outbound.pingpong.PingOutboundMessageHandler;

/**
 * The Class PingPongMessageTimer starts a timer when a ping is sent and either
 * cancels the timer when a pong has been received or times out and logs the
 * failure.
 */
public abstract class PingPongMessageTimer {
	private static final Logger log = LoggerFactory.getLogger(PingPongMessageTimer.class);

	/** The Constant TIMER, a singleton. */
	public static final PingPongMessageTimer TIMER = new PingPongMessageTimer() {

		@Override
		protected void handleFailedPing(KiSyMessage failure) {
			log.error("No pong received: {}", failure);
		}
	};

	private Map<InetSocketAddress, SubscriptionContainer> pingtasks = new ConcurrentHashMap<InetSocketAddress, SubscriptionContainer>();
	private Scheduler scheduler = Schedulers.computation();

	/**
	 * Starts a timer when a ping is sent.
	 *
	 * @param origin
	 *          the origin
	 * @param destination
	 *          the destination
	 * @see PingOutboundMessageHandler
	 */
	public void pingSent(final InetSocketAddress origin, final InetSocketAddress destination) {
		log.trace("Ping sent to {}", destination);

		Subscription sub = scheduler.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				fireConnectionLostMessage(origin, destination);
			}
		}, 30, TimeUnit.SECONDS);

		pingtasks.put(destination, new SubscriptionContainer(sub));
	}

	/**
	 * Cancels the ping timer when the pong is received and returns the elapsed
	 * time in nanoseconds. If there is no timer then -1 is returned.
	 *
	 * @param origin
	 *          the origin
	 * @return the long
	 * @see PongInboundMessageHandler
	 */
	public long pongReceived(InetSocketAddress origin) {
		SubscriptionContainer sc = pingtasks.get(origin);

		if (sc == null) {
			log.warn("Pong received with no corresponding subscription from {}", origin);
			return -1;
		}

		log.trace("Cancelling ping subscription from {}", origin);
		sc.sub.unsubscribe();

		return System.nanoTime() - sc.start;
	}

	private void fireConnectionLostMessage(InetSocketAddress origin, InetSocketAddress destination) {
		log.warn("No pong received in 30 seconds from {}", destination);

		pingtasks.remove(destination);

		KiSyMessage message = KiSyMessageCreator.createPingTime(origin, destination, -1l);

		handleFailedPing(message);
	}

	/**
	 * Handle failed ping.
	 *
	 * @param failure
	 *          the failure
	 */
	protected abstract void handleFailedPing(KiSyMessage failure);

	private static class SubscriptionContainer {
		private Subscription sub;
		private long start = System.nanoTime();

		public SubscriptionContainer(Subscription sub) {
			this.sub = sub;
		}
	}

}
