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

import org.evilco.netty.msgpack.error.MessageRegistryException;

/**
 * Declares required methods for message registries.
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public interface IMessageRegistry<T extends Object> {

	/**
	 * Searches a message identifier based on a type.
	 * @param messageType The message type.
	 * @return The identifier.
	 * @throws org.evilco.netty.msgpack.error.MessageRegistryException Occurs when a message type could not be localized.
	 */
	public short getMessageID (Class<? extends T> messageType) throws MessageRegistryException;

	/**
	 * Searches a message type based on an identifier.
	 * @param messageID The message identifier.
	 * @return The message type.
	 * @throws org.evilco.netty.msgpack.error.MessageRegistryException Occurs when a message type could not be localized.
	 */
	public Class<? extends T> getMessageType (short messageID) throws MessageRegistryException;
}
