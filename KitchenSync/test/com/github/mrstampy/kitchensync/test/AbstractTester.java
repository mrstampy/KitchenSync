package com.github.mrstampy.kitchensync.test;

public abstract class AbstractTester {

	protected AbstractTester() {
		initKiSyHandlers();
	}

	protected void initKiSyHandlers() {
		KiSyInitializer initer = new KiSyInitializer();

		initer.initInboundHandlers();
		initer.initOutboundHandlers();
	}

}
