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
package org.evilco.netty.msgpack.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * Takes care of splitting message streams within netty pipelines.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@ChannelHandler.Sharable
public class MessageFrameCodec extends ByteToMessageCodec<ByteBuf> {

	/**
	 * Stores the message codec singleton.
	 */
	private static MessageFrameCodec instance = null;

	/**
	 * Constructs a new MessageFrameCodec instance.
	 */
	private MessageFrameCodec () { }

	/**
	 * Returns the singleton instance.
	 * @return The instance.
	 */
	public static MessageFrameCodec getInstance () {
		// create a new instance
		if (instance == null) instance = new MessageFrameCodec ();

		// return cached instance
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void encode (ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		// ensure buffer is writable
		out.ensureWritable ((msg.readableBytes ()  + 4));

		// write length
		out.writeInt (msg.readableBytes ());

		// write data
		out.writeBytes (msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void decode (ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		while (in.readableBytes () >= 4) {
			// mark reader index
			in.markReaderIndex ();

			// read length
			int length = in.readInt ();

			// check whether enough data is available
			if (length > in.readableBytes ()) {
				// reset index
				in.resetReaderIndex ();

				// skip further execution due to missing data
				break;
			}

			// read buffer
			ByteBuf buffer = ctx.alloc ().buffer (length);
			in.readBytes (buffer);

			// append to output
			out.add (buffer);
		}
	}
}
