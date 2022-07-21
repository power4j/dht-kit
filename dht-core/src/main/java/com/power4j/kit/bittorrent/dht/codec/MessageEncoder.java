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

package com.power4j.kit.bittorrent.dht.codec;

import com.power4j.kit.bittorrent.core.BepDict;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.core.util.EncodeUtil;
import com.power4j.kit.bittorrent.dht.message.ErrorMessage;
import com.power4j.kit.bittorrent.dht.message.Message;
import com.power4j.kit.bittorrent.dht.message.QueryMessage;
import com.power4j.kit.bittorrent.dht.message.ResponseMessage;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public class MessageEncoder {

	private final Charset charset;

	public MessageEncoder(Charset charset) {
		this.charset = charset;
	}

	public MessageEncoder() {
		this(StandardCharsets.UTF_8);
	}

	public static byte[] toBytes(Message message) {
		return toBytes(message, StandardCharsets.UTF_8);
	}

	public static String toString(Message message) {
		return new String(toBytes(message, StandardCharsets.UTF_8));
	}

	public static byte[] toBytes(Message message, Charset charset) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream(256);
		MessageEncoder encoder = new MessageEncoder(charset);
		if (message instanceof QueryMessage) {
			encoder.encode((QueryMessage) message, stream);
		}
		else if (message instanceof ResponseMessage) {
			encoder.encode((ResponseMessage) message, stream);
		}
		else if (message instanceof ErrorMessage) {
			encoder.encode((ErrorMessage) message, stream);
		}
		else {
			throw new IllegalArgumentException("Message not support :" + message.getClass().getSimpleName());
		}
		return stream.toByteArray();
	}

	public void encode(QueryMessage message, OutputStream stream) {
		BepDict.Builder builder = BepDict.builder();
		merge(message, builder);
		builder.entry(MessageKey.KEY_QUERY_METHOD, message.getMethod());
		builder.entry(MessageKey.KEY_QUERY_ARGV, message.getArguments());
		EncodeUtil.encode(builder.build(), stream, charset);
	}

	public void encode(ResponseMessage message, OutputStream stream) {
		BepDict.Builder builder = BepDict.builder();
		merge(message, builder);
		builder.entry(MessageKey.KEY_RESPONSE_BODY, message.getBody());
		EncodeUtil.encode(builder.build(), stream, charset);
	}

	public void encode(ErrorMessage message, OutputStream stream) {
		BepDict.Builder builder = BepDict.builder();
		merge(message, builder);
		builder.entry(MessageKey.KEY_ERROR_BODY, message.getBody());
		EncodeUtil.encode(builder.build(), stream, charset);
	}

	protected void merge(Message message, BepDict.Builder builder) {
		builder.entry(MessageKey.KEY_TYPE, BepStr.ofChar(message.getType().getValue()));
		builder.entry(MessageKey.KEY_TRANSACTION_ID, message.getTransaction());
		message.getVersion().ifPresent(v -> builder.entry(MessageKey.KEY_VERSION, v));
	}

}
