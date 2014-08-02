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

# Release 2.0 - August 2, 2014

* extracted [core functionality](https://github.com/mrstampy/KitchenSync-core), KitchenSync is now only a reference implementation

# Release 1.2 - July 31, 2014

* Added isPortSpecificChannel to KiSyChannel interface, implemented in abstract superclasses
* More efficient sorting of inbound and outbound handlers
* Added [ByteBufCreator](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/payload/ByteBufCreator.java) interface to allow [KiSyChannels](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/KiSyChannel.java) to easily override the default message creation (strings, byte arrays and [KiSyMessages](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/message/KiSyMessage.java).
* generics fix in inbound and outbound managers

# Release 1.1 - July 26, 2014

* Added isMulticastChannel to KiSyChannel interface, implemented in abstract superclasses
* Code reorganization

# Quickstart

* Ivy dependency - &lt;dependency org="com.github.mrstampy" name="KitchenSync" rev="2.0"/&gt;
* [Example code](https://github.com/mrstampy/KitchenSync/tree/master/KitchenSync/test/com/github/mrstampy/kitchensync/test)

# KitchenSync Architecture

KitchenSync is a Java Library for non-centralized network communication between separate processes using the [UDP](http://en.wikipedia.org/wiki/User_Datagram_Protocol) protocol.  Channels can be created as multicast channels which allow broadcasting of messages to all connected channels, port-specific channels or next-port-available channels and are intended to be easily created and destroyed as required. It is built on top of [Netty](http://netty.io) and is designed to be simple to understand and use while providing the ability to customise individual channels.

	public class KiSyMessageChannel extends
			AbstractKiSyChannel<KiSyMessageByteBufCreator, KiSyMessageInitializer, NioDatagramChannel> {
	
		@Override
		protected KiSyMessageInitializer initializer() {
			return new KiSyMessageInitializer();
		}
	
		@Override
		protected Class<NioDatagramChannel> getChannelClass() {
			return NioDatagramChannel.class;
		}
	
		@Override
		protected KiSyMessageByteBufCreator initByteBufCreator() {
			return new KiSyMessageByteBufCreator();
		}
	
	}


This reference implementation of [KitchenSync-core](https://github.com/mrstampy/KitchenSync-core) provides a generic messaging object - [KiSyMessage](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/message/KiSyMessage.java) - which can be tailored for specific use.  Of note in this RI:

* Automatic [acknowledgement](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/message/inbound/ack/AckInboundMessageHandler.java) of message receipt
* Automatic [pong](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/message/inbound/pingpong/PingInboundMessageHandler.java) responses for received pings
* Implementations of [channel initializer](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/initializer/KiSyMessageInitializer.java), [channel inbound handler](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/handler/KiSyMessageHandler.java), and [ByteBuf creator](https://github.com/mrstampy/KitchenSync/blob/master/KitchenSync/src/com/github/mrstampy/kitchensync/netty/channel/payload/KiSyMessageByteBufCreator.java) necessary to process KiSyMessage payloads

