package com.github.mrstampy.kitchensync.message.outbound;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Action1;

public class KiSyOutboundMessageManager {
	private static final Logger log = LoggerFactory.getLogger(KiSyOutboundMessageManager.class);

	private List<KiSyOutboundMessageHandler<?>> handlers = new ArrayList<KiSyOutboundMessageHandler<?>>();

	public static final KiSyOutboundMessageManager INSTANCE = new KiSyOutboundMessageManager();

	private HandlerComparator comparator = new HandlerComparator();

	protected KiSyOutboundMessageManager() {

	}

	public void addOutboundHandlers(KiSyOutboundMessageHandler<?>... hndlrs) {
		for(KiSyOutboundMessageHandler<?> handler : hndlrs) {
			if (!handlers.contains(handler)) handlers.add(handler);
		}
	}

	public void removeOutboundHandler(KiSyOutboundMessageHandler<?> handler) {
		handlers.remove(handler);
	}

	public void clearOutboundHandlers() {
		handlers.clear();
	}

	public <MSG> void presend(MSG message, InetSocketAddress originator, InetSocketAddress recipient) {
		List<KiSyOutboundMessageHandler<MSG>> relevant = getHandlersForMessage(message, recipient);
		if (relevant.isEmpty()) return;

		final MSG msg = message;
		final InetSocketAddress org = originator;
		final InetSocketAddress isa = recipient;
		Observable.from(relevant).subscribe(new Action1<KiSyOutboundMessageHandler<MSG>>() {

			@Override
			public void call(KiSyOutboundMessageHandler<MSG> t1) {
				t1.presend(msg, org, isa);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private <MSG> List<KiSyOutboundMessageHandler<MSG>> getHandlersForMessage(MSG message, InetSocketAddress recipient) {
		List<KiSyOutboundMessageHandler<MSG>> relevant = new ArrayList<KiSyOutboundMessageHandler<MSG>>();

		ListIterator<KiSyOutboundMessageHandler<?>> it = handlers.listIterator();

		while (it.hasNext()) {
			try {
				KiSyOutboundMessageHandler<MSG> handler = (KiSyOutboundMessageHandler<MSG>) it.next();
				if (handler.isForMessage(message, recipient)) relevant.add(handler);
			} catch (Exception e) {
				log.debug("Handler not typed for {}", message, e);
			}
		}

		if (!relevant.isEmpty()) Collections.sort(relevant, comparator);

		return relevant;
	}

	private static class HandlerComparator implements Comparator<KiSyOutboundMessageHandler<?>> {

		@Override
		public int compare(KiSyOutboundMessageHandler<?> kisy1, KiSyOutboundMessageHandler<?> kisy2) {
			return kisy1.getExecutionOrder() - kisy2.getExecutionOrder();
		}

	}
}
