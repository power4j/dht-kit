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

package com.power4j.kit.bittorrent.peer.message;

import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/20
 * @since 1.0
 */
public enum MessageId {

	/**
	 * choke
	 */
	CHOKE(0),
	/**
	 * un-choke
	 */
	UN_CHOKE(1),
	/**
	 * interested
	 */
	INTERESTED(2),
	/**
	 * not interested
	 */
	NOT_INTERESTED(3),
	/**
	 * have
	 */
	HAVE(4),
	/**
	 * bitfield
	 */
	BITFIELD(5),
	/**
	 * request
	 */
	REQUEST(6),
	/**
	 * piece
	 */
	PIECE(7),
	/**
	 * cancel
	 */
	CANCEL(8),
	/**
	 * extended message
	 */
	EXTENDED(20);

	private final int value;

	MessageId(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	/**
	 * Parse form value
	 * @param value the value
	 * @return Optional value
	 */
	public static Optional<MessageId> parseValue(final int value) {
		for (MessageId o : MessageId.values()) {
			if (o.value == value) {
				return Optional.of(o);
			}
		}
		return Optional.empty();
	}

}
