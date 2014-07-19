package com.github.mrstampy.kitchensync.message.handler;

import io.netty.channel.socket.DatagramChannel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;

@SuppressWarnings("rawtypes")
public class KiSyInboundMessageManager<MSG> {
	private static final BigDecimal ONE_MILLION = new BigDecimal(1000000);

	private static final Logger log = LoggerFactory.getLogger(KiSyInboundMessageManager.class);

	public static final KiSyInboundMessageManager<KiSyMessage> INSTANCE = new KiSyInboundMessageManager<KiSyMessage>();

	private List<KiSyInboundMesssageHandler> messageHandlers = new ArrayList<KiSyInboundMesssageHandler>();

	private HandlerComparator<MSG> handlerComparator = new HandlerComparator<MSG>();

	private MessageLocker locker = new MessageLocker();

	private Scheduler scheduler = Schedulers.computation();

	public void addMessageHandlers(KiSyInboundMesssageHandler... handlers) {
		if (handlers == null || handlers.length == 0) return;

		addAllMessageHandlers(Arrays.asList(handlers));
	}

	public void addAllMessageHandlers(Collection<KiSyInboundMesssageHandler> handlers) {
		messageHandlers.addAll(handlers);
	}

	public void removeMessageHandler(KiSyInboundMesssageHandler handler) {
		messageHandlers.remove(handler);
	}

	public void clearMessageHandlers() {
		messageHandlers.clear();
	}

	public <CHANNEL extends KiSyChannel<DatagramChannel, MSG>> void processMessage(MSG message, CHANNEL channel) {
		log.trace("Processing message {}", message);

		long start = System.nanoTime();

		List<KiSyInboundMesssageHandler<MSG, CHANNEL>> ordered = getHandlersForMessage(message);

		if (ordered.isEmpty()) {
			log.debug("No messages handlers for {}", message);
			return;
		}

		CountDownLatch cdl = new CountDownLatch(ordered.size());

		scheduleCleanup(message, cdl, start);
		processMessage(ordered, message, channel, cdl);
	}

	private <CHANNEL extends KiSyChannel<DatagramChannel, MSG>> void processMessage(
			List<KiSyInboundMesssageHandler<MSG, CHANNEL>> ordered, MSG message, CHANNEL channel, final CountDownLatch cdl) {
		final MSG msg = message;
		final CHANNEL ch = channel;
		Observable.from(ordered, scheduler).subscribe(new Action1<KiSyInboundMesssageHandler<MSG, CHANNEL>>() {

			@Override
			public void call(KiSyInboundMesssageHandler<MSG, CHANNEL> t1) {
				try {
					t1.messageReceived(msg, ch);
				} catch (Exception e) {
					log.error("Could not process message {} with {}", message, t1, e);
				} finally {
					cdl.countDown();
				}
			}
		});
	}

	private void scheduleCleanup(final MSG message, final CountDownLatch cdl, final long start) {
		scheduler.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				try {
					boolean done = cdl.await(10, TimeUnit.SECONDS);
					if (done) {
						if (log.isTraceEnabled()) {
							long end = System.nanoTime();
							log.trace("Message {} fully processed in {} ms", message, getTimeInMillis(end - start));
						}
					} else {
						log.warn("Message processing > 10 seconds: {}", message);
					}
				} catch (InterruptedException e) {
					log.error("Unexpected exception", e);
				}

				locker.removeLock(message);
			}

			private String getTimeInMillis(long l) {
				return new BigDecimal(l).divide(ONE_MILLION, 3, RoundingMode.HALF_UP).toPlainString();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private <CHANNEL extends KiSyChannel<DatagramChannel, MSG>> List<KiSyInboundMesssageHandler<MSG, CHANNEL>> getHandlersForMessage(
			MSG message) {
		List<KiSyInboundMesssageHandler<MSG, CHANNEL>> ordered = new ArrayList<KiSyInboundMesssageHandler<MSG, CHANNEL>>();

		ListIterator<KiSyInboundMesssageHandler> it = messageHandlers.listIterator();
		while (it.hasNext()) {
			try {
				KiSyInboundMesssageHandler<MSG, ?> handler = it.next();
				if (handler.canHandleMessage(message)) ordered.add((KiSyInboundMesssageHandler<MSG, CHANNEL>) handler);
			} catch (Exception e) {
				log.debug("Handler not applicable for message {}", message, e);
			}
		}

		if (!ordered.isEmpty()) Collections.sort(ordered, handlerComparator);

		return ordered;
	}

	private static class HandlerComparator<MSG> implements Comparator<KiSyInboundMesssageHandler<MSG, ?>> {

		@Override
		public int compare(KiSyInboundMesssageHandler<MSG, ?> kisy1, KiSyInboundMesssageHandler<MSG, ?> kisy2) {
			return kisy1.getExecutionOrder() - kisy2.getExecutionOrder();
		}

	}

}
