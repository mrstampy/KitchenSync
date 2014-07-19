package com.github.mrstampy.kitchensync.message.handler.pingpong;

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

public abstract class PingPongMessageTimer {
	private static final Logger log = LoggerFactory.getLogger(PingPongMessageTimer.class);

	public static final PingPongMessageTimer TIMER = new PingPongMessageTimer() {

		@Override
		protected void handleFailedPing(KiSyMessage failure) {
			log.error("No pong received: {}", failure);
		}
	};

	private Map<InetSocketAddress, SubscriptionContainer> pingtasks = new ConcurrentHashMap<InetSocketAddress, SubscriptionContainer>();
	private Scheduler scheduler = Schedulers.computation();

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

	public long pongReceived(InetSocketAddress origin) {
		SubscriptionContainer sc = pingtasks.get(origin);

		if (sc == null) {
			log.warn("Pong received with no corresponding subscription from {}", origin);
			return -1;
		}

		log.trace("Cancelling ping subscription from {}", origin);
		sc.sub.unsubscribe();

		return System.currentTimeMillis() - sc.start;
	}

	private void fireConnectionLostMessage(InetSocketAddress origin, InetSocketAddress destination) {
		log.warn("No pong received in 30 seconds from {}", destination);

		pingtasks.remove(destination);

		KiSyMessage message = KiSyMessageCreator.createPingTime(origin, destination, -1l);

		handleFailedPing(message);
	}

	protected abstract void handleFailedPing(KiSyMessage failure);

	private static class SubscriptionContainer {
		private Subscription sub;
		private long start = System.currentTimeMillis();

		public SubscriptionContainer(Subscription sub) {
			this.sub = sub;
		}
	}

}
