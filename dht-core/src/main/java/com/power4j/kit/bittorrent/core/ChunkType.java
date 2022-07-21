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

package com.power4j.kit.bittorrent.core;

import com.power4j.kit.bittorrent.common.Constant;

import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/6/20
 * @since 1.0
 */
public enum ChunkType {

	/**
	 * String
	 */
	STR("String"),
	/**
	 * Integer
	 */
	INT("Integer"),
	/**
	 * List
	 */
	LIST("List"),
	/**
	 * Dictionary
	 */
	DICT("Dictionary");

	private final String value;

	ChunkType(String value) {
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
	public static Optional<ChunkType> parse(final String value) {
		if (value == null) {
			return Optional.empty();
		}
		for (ChunkType o : ChunkType.values()) {
			if (o.getValue().equals(value)) {
				return Optional.of(o);
			}
		}
		return Optional.empty();
	}

	/**
	 * Get ElementType from value
	 * @param value the value
	 * @return 如果解析失败抛出 IllegalArgumentException
	 * @throws IllegalArgumentException The value is invalid
	 */
	public static ChunkType fromValue(final String value) throws IllegalArgumentException {
		return parse(value).orElseThrow(() -> new IllegalArgumentException("Invalid value : " + value));
	}

	/**
	 * Get ElementType from type token
	 * @param token token (byte value)
	 * @return return empty means unknown
	 */
	public static Optional<ChunkType> fromToken(int token) {
		switch (token) {
			case BepObject.FLAG_DICT:
				return Optional.of(DICT);
			case BepObject.FLAG_LIST:
				return Optional.of(LIST);
			case BepObject.FLAG_INT:
				return Optional.of(INT);
			default:
				return (token >= Constant.CHAR_ZERO && token <= Constant.CHAR_NINE) ? Optional.of(STR)
						: Optional.empty();
		}
	}

}
