package com.github.mrstampy.kitchensync.message.outbound;

import java.net.InetSocketAddress;

public interface KiSyOutboundMessageHandler<MSG> {

	public static final int DEFAULT_EXECUTION_ORDER = 100;

	boolean isForMessage(MSG message, InetSocketAddress recipient);

	void presend(MSG message, InetSocketAddress originator, InetSocketAddress recipient);

	int getExecutionOrder();
}
