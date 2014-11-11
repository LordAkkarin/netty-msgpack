/*
 * Copyright 2014 Johannes Donath <johannesd@evil-co.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.evilco.netty.msgpack.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.evilco.netty.msgpack.codec.MessageCodec;
import org.evilco.netty.msgpack.registry.AbstractMessageRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;

import java.io.IOException;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RunWith (MockitoJUnitRunner.class)
public class MessageCodecTest {

	/**
	 * Stores the embedded channel.
	 */
	private EmbeddedChannel channel = null;

	/**
	 * Stores the message codec.
	 */
	private MessageCodec codec = null;

	/**
	 * Stores the registry.
	 */
	private TestRegistry registry = null;

	/**
	 * Prepares the tests.
	 */
	@Before
	public void setup () {
		this.registry = new TestRegistry ();
		this.codec = new MessageCodec (this.registry);
		this.channel = new EmbeddedChannel (this.codec);
	}

	/**
	 * Tests message encoding.
	 */
	@Test
	public void encoding () {
		// create a test message
		this.channel.writeOutbound (new TestMessage1 (42));
		this.channel.writeOutbound (new TestMessage2 (42, 21));

		// verify generated value
		Assert.assertEquals ("Not all messages were encoded", 2, this.channel.outboundMessages ().size ());

		// get first buffer
		ByteBuf buffer1 = ((ByteBuf) this.channel.outboundMessages ().poll ());

		// verify buffer1
		Assert.assertEquals ("Packet identifier does not match", 0x00, buffer1.readShort ());
		Assert.assertEquals ("Packet length does not match", 2, buffer1.readableBytes ());

		// get second buffer
		ByteBuf buffer2 = ((ByteBuf) this.channel.outboundMessages ().poll ());

		// verify buffer2
		Assert.assertEquals ("Packet identifier does not match", 0x10, buffer2.readShort ());
		Assert.assertEquals ("Packet length does not match", 3, buffer2.readableBytes ());
	}

	/**
	 * Tests message decoding.
	 */
	@Test
	public void decoding () throws IOException {
		// create a msgpack instance
		MessagePack messagePack = new MessagePack ();

		// create ByteBuf
		ByteBuf buffer = this.channel.alloc ().buffer ();

		// generate a message
		buffer.writeShort (0x00);
		buffer.writeBytes (messagePack.write (new TestMessage1 (42)));

		// write
		this.channel.writeInbound (buffer);

		// create ByteBuf
		buffer = this.channel.alloc ().buffer ();

		// generate a message
		buffer.writeShort (0x10);
		buffer.writeBytes (messagePack.write (new TestMessage2 (42, 21)));

		// write
		this.channel.writeInbound (buffer);

		// check inbound queue
		Assert.assertEquals ("Less than two messages decoded", 2, this.channel.inboundMessages ().size ());

		// get first object
		TestMessage1 message1 = ((TestMessage1) this.channel.inboundMessages ().poll ());
		TestMessage2 message2 = ((TestMessage2) this.channel.inboundMessages ().poll ());

		// verify values
		Assert.assertEquals ("Test value one does not match", 42, message1.getValue ());
		Assert.assertEquals ("Test value one does not match", 42, message2.getValue ());
		Assert.assertEquals ("Test value two does not match", 21, message2.getValue2 ());
	}

	/**
	 * A test registry.
	 */
	public class TestRegistry extends AbstractMessageRegistry {

		/**
		 * Constructs a new TestRegistry instance.
		 */
		public TestRegistry () {
			super ();

			this.registerMessage (0x00, TestMessage1.class);
			this.registerMessage (0x10, TestMessage2.class);
		}
	}

	/**
	 * Represents a test message.
	 */
	@AllArgsConstructor
	@Message
	public static class TestMessage1 {

		@Getter
		private int value;

		/**
		 * Default Constructor
		 */
		public TestMessage1 () { }
	}

	/**
	 * Represents a test message.
	 */
	@Message
	public static class TestMessage2 extends TestMessage1 {

		@Getter
		private int value2;

		/**
		 * Default Constructor
		 */
		public TestMessage2 () { }

		/**
		 * Constructs a new TestMessage2 instance.
		 * @param value The value.
		 * @param value2 The second value.
		 */
		public TestMessage2 (int value, int value2) {
			super (value);
			this.value2 = value2;
		}
	}
}
