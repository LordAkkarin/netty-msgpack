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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.evilco.netty.msgpack.registry.IMessageRegistry;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class MessageCodec extends ByteToMessageCodec<Object> {

	/**
	 * Stores the active message registry.
	 */
	@Getter
	@Setter
	@NonNull
	private IMessageRegistry registry;

	/**
	 * Stores the msgpack instance.
	 */
	@Getter (AccessLevel.PROTECTED)
	@Setter (AccessLevel.PROTECTED)
	@NonNull
	private MessagePack messagePack = new MessagePack ();

	/**
	 * Constructs a new MessageCodec instance.
	 * @param messageRegistry The registry.
	 */
	public MessageCodec (IMessageRegistry messageRegistry) {
		this.setRegistry (messageRegistry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void encode (ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		// verify argument
		Preconditions.checkNotNull (msg, "msg");

		// search message identifier
		short identifier = this.getRegistry ().getMessageID (msg.getClass ());

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
		Class<?> type = this.getRegistry ().getMessageType (identifier);

		// read data
		byte[] data = new byte[in.readableBytes ()];
		in.readBytes (data);

		// decode message
		out.add (this.messagePack.read (data, type));
	}
}
