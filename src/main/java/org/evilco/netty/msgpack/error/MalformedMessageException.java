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
package org.evilco.netty.msgpack.error;

/**
 * @author Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.com>
 */
public class MalformedMessageException extends MessageRegistryException {

	/**
	 * Constructs a new MalformedMessageException instance.
	 */
	public MalformedMessageException () {
		super ();
	}

	/**
	 * Constructs a new MalformedMessageException instance.
	 * @param message The error message.
	 */
	public MalformedMessageException (String message) {
		super (message);
	}

	/**
	 * Constructs a new MalformedMessageException instance.
	 * @param message The error message.
	 * @param cause The error cause.
	 */
	public MalformedMessageException (String message, Throwable cause) {
		super (message, cause);
	}

	/**
	 * Constructs a new MalformedMessageException instance.
	 * @param cause The error cause.
	 */
	public MalformedMessageException (Throwable cause) {
		super (cause);
	}
}
