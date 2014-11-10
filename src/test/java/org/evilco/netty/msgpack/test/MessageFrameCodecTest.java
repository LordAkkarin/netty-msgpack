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
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.evilco.netty.msgpack.codec.MessageFrameCodec;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@RunWith (MockitoJUnitRunner.class)
public class MessageFrameCodecTest {

	/**
	 * Stores the active channel.
	 */
	private EmbeddedChannel channel = null;

	/**
	 * Stores the frame codec.
	 */
	private MessageFrameCodec frameCodec = null;

	/**
	 * Prepares the test.
	 */
	@Before
	public void setup () {
		this.frameCodec = MessageFrameCodec.getInstance ();
		this.channel = new EmbeddedChannel (this.frameCodec);
	}

	/**
	 * Tests encoding.
	 */
	@Test
	public void encoding () {
		// create a test message
		ByteBuf message = this.channel.alloc ().buffer ();
		message.writeInt (42);

		// write data
		Assert.assertTrue (this.channel.writeOutbound (message));

		// grab output
		ByteBuf output = this.channel.readOutbound ();

		// verify packet
		Assert.assertEquals ("Overall packet size does not match", 8, output.readableBytes ());
		Assert.assertEquals ("Packet #0: Packet length header does not match", 4, output.readInt ());
		Assert.assertEquals ("Packet #0: Packet body does not match", 42, output.readInt ());
	}

	/**
	 * Tests decoding.
	 */
	@Test
	public void decoding () {
		// create a test message
		ByteBuf message = this.channel.alloc ().buffer ();
		message.writeInt (8);
		message.writeInt (42);
		message.writeInt (21);
		message.writeInt (8);
		message.writeInt (42);
		message.writeInt (21);

		// write data
		Assert.assertTrue (this.channel.writeInbound (message));

		// initialize count
		int i = 0;

		// iterate over elements in stream
		while (this.channel.inboundMessages ().peek () != null) {
			ByteBuf input = ((ByteBuf) this.channel.inboundMessages ().poll ());

			// check length
			Assert.assertEquals ("Packet #" + i + ": Decoded packet size does not match", 8, input.readableBytes ());
			Assert.assertEquals ("Packet #" + i + ": First field does not match", 42, input.readInt ());
			Assert.assertEquals ("Packet #" + i + ": Second field does not match", 21, input.readInt ());

			// update count
			i++;
		}
	}
}
