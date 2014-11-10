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

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.*;
import org.evilco.netty.msgpack.registry.IMessageRegistry;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
@AllArgsConstructor
public class MessageCodec<T extends Object> extends ByteToMessageCodec<T> {

	/**
	 * Stores the active message registry.
	 */
	@Getter
	@Setter
	@NonNull
	private IMessageRegistry<T> registry;

	/**
	 * Stores the msgpack instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	@Setter (AccessLevel.PROTECTED)
	@NonNull
	private MessagePack messagePack = new MessagePack ();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void encode (ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
		// verify argument
		Preconditions.checkNotNull (msg, "msg");

		// search message identifier
		short identifier = this.getRegistry ().getMessageID (msg);

		// send identifier
		out.writeShort (identifier);

		// encode message
		out.writeBytes (this.messagePack.write (msg));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void decode (ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// read identifier
		short identifier = in.readShort ();

		// search message type
		Class<T> type = this.getRegistry ().getMessageType (identifier);

		// decode message
		out.add (this.messagePack.read (in.array (), type));
	}
}