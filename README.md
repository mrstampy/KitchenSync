# A Java Library for Distributed Communication

You are special agent Michael Roedick in charge of covert ops software development at the NSA.  Keith Alexander has just called you into his [office](http://www.businessinsider.com.au/the-us-army-star-trek-command-center-2013-9) to discuss the requirements for software to support a covert programme.

"Hi Michael, thanks for beaming in.  Good to see you.  Take a seat.  I trust you read the brief regarding [Operation FUBAR](http://www.urbandictionary.com/define.php?term=gang%20stalking)?"

"Yes I did.  So what kind of software do the operatives need?"

"It has to allow communication between field operatives and allow broadcasting of messages to groups.  This includes text, images, videos…"

"Ok, what is the use case for it?"

"Right. The [TI](http://www.urbandictionary.com/define.php?term=targeted%20individual) will be monitored by agents around the domicile.  The software needs to be able to send false data to other operatives on a regular basis to provide plausible deniability.  The operation data will be encrypted and the connections between operatives must be encrypted.   When the TI leaves the agents around the domicile will need to send messages to alert agents in the field of the TI's movements. It needs to happen in near-realtime in order to coordinate the various operations each group must undertake."

"Ok, so the [street theatre](http://www.urbandictionary.com/define.php?term=street%20theater) operatives and [gaslighting](http://www.urbandictionary.com/define.php?term=Gaslighting) operatives can coordinate based on whether the TI is in the vicinity or not, right?"

"Exactly.  And it needs to be able to send pictures and video to either individual operatives or groups.  The groups need to be easily created, communicated and destroyed on demand.  And you'll need to use some open source libraries to disguise the software.  You know, like what we did for Linux.  Say, you still in touch with Linus?"

"I send him a fruit basket every Christmas.  I think I've got it, and I know where to start."

You rise to leave when you hear your boss - "Ah ah ah - what do we say?"

"Whoops...Permission to leave the bridge sir."

"Permission granted."

Special Agent Roedick, KitchenSync is just the thing for you!

# Quickstart

* Ivy dependency - &lt;dependency org="com.github.mrstampy" name="KitchenSync" rev="1.0"/&gt;
* [Example code](https://github.com/mrstampy/KitchenSync/tree/master/KitchenSync/test/com/github/mrstampy/kitchensync/test)

# KitchenSync Architecture

KitchenSync is a Java Library for non-centralized network communication between separate processes using the [UDP](http://en.wikipedia.org/wiki/User_Datagram_Protocol) protocol.  Channels can be created as multicast channels which allow broadcasting of messages to all connected channels, port-specific channels or next-port-available channels and are intended to be easily created and destroyed as required. It is built on top of [Netty](http://netty.io) and is designed to be simple to understand and use while providing the ability to customise individual channels.  

Two interfaces - [KiSyChannel](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/KiSyChannel.java) and [KiSyMulticastChannel](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/KiSyMulticastChannel.java) - provide the API for network communication.  Three abstract implementations - [AbstractKiSyChannel](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/AbstractKiSyChannel.java), [AbstractPortSpecificKiSyChannel](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/AbstractPortSpecificKiSyChannel.java), and [AbstractKiSyMulticastChannel](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/AbstractKiSyMulticastChannel.java) - exist for ease of channel creation:

	protected KiSyChannel initChannel() {
		AbstractKiSyChannel channel = new AbstractKiSyChannel() {

			@Override
			protected ChannelInitializer<DatagramChannel> initializer() {
				return new SslInitializer();
			}

			@Override
			protected Class<? extends DatagramChannel> getChannelClass() {
				return NioDatagramChannel.class;
			}

		};

		channel.bind();

		return channel;
	}

The [ChannelInitializer](http://netty.io/4.0/api/io/netty/channel/ChannelInitializer.html) is a Netty class which is used to initialise a [Bootstrap](http://netty.io/4.0/api/io/netty/bootstrap/Bootstrap.html) object for the channel and is ignored if the Bootstrap [already exists](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/DefaultChannelRegistry.java).  [Netty channel handlers](http://netty.io/4.0/api/io/netty/channel/ChannelHandler.html) are added to the channel's pipeline in the implementation of the ChannelInitializer to control the channel's behaviour such as using SSL for connections:

	@Override
	protected void initChannel(DatagramChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new SslHandler(context.createSSLEngine()));
		pipeline.addLast(new KiSyMessageHandler());
	}

KiSyChannels can send and receive one of three types of messages by default - [byte arrays](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/initializer/ByteArrayMessageInitializer.java), [strings](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/initializer/StringMessageInitializer.java) or [KiSyMessage](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/initializer/KiSyMessageInitializer.java)s.

## Inbound and Outbound

KitchenSync adds to the Netty architecture - which provides the ability to add custom handlers to interface with applications - by providing [inbound](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/message/inbound/KiSyInboundMessageManager.java) and [outbound](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/message/outbound/KiSyOutboundMessageManager.java) message managers which are initialised on application startup to apply [inbound](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/message/inbound/KiSyInboundMesssageHandler.java) and [outbound](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/message/outbound/KiSyOutboundMessageHandler.java) application specific KitchenSync handler implementations to messages.  This separates channel configuration (in the ChannelInitializer) from message processing such as logging of messages, persistence of messages, autonomous event triggering on message, etc.  The handlers' execution is ordered to allow sequential operations to take place.  Note that the handlers exist for preparation of messages for processing by the application and execution of any presend logic.  Any significant processing of the message should be done on a separate thread.  Strictly speaking only one implementation is necessary - inbound, to pull messages into the application.


