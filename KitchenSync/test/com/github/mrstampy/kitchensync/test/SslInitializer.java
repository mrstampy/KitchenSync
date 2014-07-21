package com.github.mrstampy.kitchensync.test;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.ssl.SslHandler;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.kitchensync.netty.channel.initializer.KiSyMessageInitializer;

public class SslInitializer extends ChannelInitializer<DatagramChannel> {
	private static final Logger log = LoggerFactory.getLogger(SslInitializer.class);

	private static SSLContext context;

	static {
		try {
			initSslContext();
		} catch (Exception e) {
			log.error("Could not create dummy certificates", e);
		}
	}

	private static void initSslContext() throws Exception {
		char[] storepass = "password".toCharArray();
		char[] keypass = "password".toCharArray();
		String storename = "KitchenSyncTesting.jks";

		context = SSLContext.getInstance("TLS");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		FileInputStream fin = new FileInputStream(storename);
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(fin, storepass);

		kmf.init(ks, keypass);
		context.init(kmf.getKeyManagers(), null, null);
	}

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new SslHandler(context.createSSLEngine()));
		pipeline.addLast(new KiSyMessageInitializer());
	}

}
