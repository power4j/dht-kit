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

package com.power4j.kit.bittorrent.dht;

import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public enum QueryMethod {

	/**
	 * ping
	 */
	PING("ping"),
	/**
	 * find nood
	 */
	FIND_NODE("find_node"),
	/**
	 * get peers
	 */
	GET_PEERS("get_peers"),
	/**
	 * announce peer
	 */
	ANNOUNCE_PEER("announce_peer");

	private final String value;

	QueryMethod(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Parse form value
	 * @param value the value
	 * @return return empty if parse fail
	 */
	public static Optional<QueryMethod> parse(final String value) {
		if (value == null) {
			return Optional.empty();
		}
		for (QueryMethod o : QueryMethod.values()) {
			if (o.getValue().equals(value)) {
				return Optional.of(o);
			}
		}
		return Optional.empty();
	}

	/**
	 * Get QueryMethod from value
	 * @param value the value
	 * @return 如果解析失败抛出 IllegalArgumentException
	 * @throws IllegalArgumentException The value is invalid
	 */
	public static QueryMethod fromValue(final String value) throws IllegalArgumentException {
		return parse(value).orElseThrow(() -> new IllegalArgumentException("Invalid value : " + value));
	}

}
