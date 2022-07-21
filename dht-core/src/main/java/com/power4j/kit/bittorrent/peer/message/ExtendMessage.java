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

import org.springframework.lang.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/21
 * @since 1.0
 */
public class ExtendMessage {

	private final MessageIdEx id;

	@Nullable
	private final byte[] payload;

	public ExtendMessage(MessageIdEx id, @Nullable byte[] payload) {
		this.id = id;
		this.payload = payload;
	}

	public MessageIdEx getId() {
		return id;
	}

	public int payloadSize() {
		return payload == null ? 0 : payload.length;
	}

	public Optional<byte[]> getPayload() {
		return Optional.ofNullable(payload);
	}

	public byte[] requirePayload() {
		return getPayload().orElseThrow(NoSuchElementException::new);
	}

}
