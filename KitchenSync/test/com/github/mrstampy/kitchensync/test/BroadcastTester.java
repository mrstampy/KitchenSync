package com.github.mrstampy.kitchensync.test;

import java.net.UnknownHostException;

import com.github.mrstampy.kitchensync.message.KiSyMessage;
import com.github.mrstampy.kitchensync.message.KiSyMessageType;
import com.github.mrstampy.kitchensync.netty.channel.impl.DefaultKiSyMulticastChannel;

public class BroadcastTester extends AbstractTester {
	 private static final String MULTICAST_IP = "FF05:0:0548:c4e6:796c:0:de66:FC";
	 private static final int MULTICAST_PORT = 57962;

	private DefaultKiSyMulticastChannel michael;
	private DefaultKiSyMulticastChannel robert;
	private DefaultKiSyMulticastChannel paul;
	
	private void init() throws UnknownHostException {
		michael = createMulticastChannel();
		robert = createMulticastChannel();
		paul = createMulticastChannel();
	}
	
	public void execute() {
		KiSyMessage message = new KiSyMessage(michael.getMulticastAddress(), KiSyMessageType.INFO);
		message.addMessage("greeting", "A good day to you all!");
		
		michael.leaveGroup();
		
		michael.broadcast(message);
		
		michael.joinGroup();
	}
	
	protected DefaultKiSyMulticastChannel createMulticastChannel() throws UnknownHostException {
		DefaultKiSyMulticastChannel channel = new DefaultKiSyMulticastChannel(MULTICAST_IP, MULTICAST_PORT);
		
		channel.multicastBind();
		
		return channel;
	}

	public static void main(String[] args) throws Exception {
		BroadcastTester tester = new BroadcastTester();
		
		tester.init();
		
		tester.execute();
	}

}
