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

import com.github.mrstampy.kitchensync.netty.handler.KiSyMessageHandler;

/**
 * SSL Initializer for test classes using a self signed certificate
 * (KitchenSyncTesting.jks).
 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	 */
	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new SslHandler(context.createSSLEngine()));
		pipeline.addLast(new KiSyMessageHandler());
	}

}
