/*
 * Copyright 2021 ChenJun (power4j@outlook.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.power4j.kit.bittorrent.dht.message;

import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public enum MessageType {

	/**
	 * query
	 */
	QUERY('q'),
	/**
	 * response
	 */
	RESPONSE('r'),
	/**
	 * error
	 */
	ERROR('e');

	private final char value;

	MessageType(char value) {
		this.value = value;
	}

	public char getValue() {
		return value;
	}

	/**
	 * Parse form value
	 * @param value the value
	 * @return return empty if parse fail
	 */
	public static Optional<MessageType> parse(final char value) {
		for (MessageType o : MessageType.values()) {
			if (o.getValue() == value) {
				return Optional.of(o);
			}
		}
		return Optional.empty();
	}

}
