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
package org.evilco.netty.msgpack.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.NonNull;
import org.evilco.netty.msgpack.error.MessageRegistryException;
import org.evilco.netty.msgpack.error.UnknownMessageException;

/**
 * Provides a basic packet registry implementation.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public abstract class AbstractMessageRegistry<T> implements IMessageRegistry<T> {

	/**
	 * Stores the registry.
	 */
	private BiMap<Short, Class<? extends T>> registry = HashBiMap.create ();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getMessageID (@NonNull Class<? extends T> messageType) throws MessageRegistryException {
		// verify existence
		if (this.registry.inverse ().containsKey (messageType)) throw new UnknownMessageException ("Could not find registration for type " + messageType.getName ());

		// get registered identifier
		return this.registry.inverse ().get (messageType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends T> getMessageType (short messageID) throws MessageRegistryException {
		// verify existence
		if (this.registry.containsKey (messageID)) throw new UnknownMessageException ("Could not find registration for identifier " + messageID);

		// get registered type
		return this.registry.get (messageID);
	}

	/**
	 * Registers a message type.
	 * @param messageID The message identifier.
	 * @param messageType The message type.
	 */
	protected void registerMessage (short messageID, @NonNull Class<? extends T> messageType) {
		this.registry.put (messageID, messageType);
	}

	/**
	 * Alias for {@link org.evilco.netty.msgpack.registry.AbstractMessageRegistry#registerMessage(short, Class)}
	 * @see {@link org.evilco.netty.msgpack.registry.AbstractMessageRegistry#registerMessage(short, Class)}
	 */
	protected void registerMessage (int messageID, @NonNull Class<? extends T> messageType) {
		this.registerMessage (((short) messageID), messageType);
	}
}
