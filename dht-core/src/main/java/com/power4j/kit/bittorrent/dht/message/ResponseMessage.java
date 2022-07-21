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

import com.power4j.kit.bittorrent.core.BepDict;
import com.power4j.kit.bittorrent.core.BepInt;
import com.power4j.kit.bittorrent.core.BepObject;
import com.power4j.kit.bittorrent.core.BepStr;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public class ResponseMessage extends AbstractMessage {

	private final BepDict body;

	public ResponseMessage(BepStr transaction, @Nullable BepStr version, BepDict body) {
		super(transaction, version);
		this.body = body;
	}

	public BepDict getBody() {
		return body;
	}

	@Override
	public MessageType getType() {
		return MessageType.RESPONSE;
	}

	public static class Builder {

		private BepStr transaction;

		@Nullable
		private BepStr version;

		private final BepDict body = new BepDict();

		public Builder transaction(BepStr value) {
			this.transaction = value;
			return this;
		}

		public Builder version(@Nullable BepStr value) {
			this.version = value;
			return this;
		}

		public Builder body(String key, BepObject value) {
			body.put(key, value);
			return this;
		}

		public Builder body(String key, String value, Charset charset) {
			return body(key, BepStr.of(value, charset));
		}

		public Builder body(String key, String value) {
			return body(key, BepStr.of(value, StandardCharsets.UTF_8));
		}

		public Builder body(String key, long value) {
			return body(key, BepInt.of(value));
		}

		public ResponseMessage build() {
			Validate.notNull(transaction);
			return new ResponseMessage(transaction, version, body);
		}

	}

}
