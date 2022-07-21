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
import com.power4j.kit.bittorrent.core.BepInt;
import com.power4j.kit.bittorrent.core.BepList;
import com.power4j.kit.bittorrent.core.BepObject;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.core.io.StreamReader;
import com.power4j.kit.bittorrent.dht.message.ErrorMessage;
import com.power4j.kit.bittorrent.dht.message.Message;
import com.power4j.kit.bittorrent.dht.message.MessageType;
import com.power4j.kit.bittorrent.dht.message.QueryMessage;
import com.power4j.kit.bittorrent.dht.message.ResponseMessage;
import com.power4j.kit.bittorrent.exceptions.ProtocolException;
import com.power4j.kit.bittorrent.exceptions.BepException;
import com.power4j.kit.bittorrent.exceptions.EncodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
@Slf4j
public class MessageDecoder {

	private final static int ERROR_PARTS = 2;

	private final Charset charset;

	public MessageDecoder(Charset charset) {
		this.charset = charset;
	}

	public MessageDecoder() {
		this(StandardCharsets.UTF_8);
	}

	/**
	 * 解码消息
	 * @param stream 输入
	 * @return Message
	 * @throws EncodeException Encode Error,e.g. IO error
	 * @throws ProtocolException Bad message
	 */
	public Message decode(InputStream stream) {
		BepDict dict;
		try {
			dict = StreamReader.of(stream).readDict();
		}
		catch (BepException e) {
			throw new ProtocolException("Message unreadable", e);
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
		MessageType type = getMessageType(dict).orElseThrow(() -> new ProtocolException("Invalid message type"));
		BepStr tid = getTransactionId(dict).orElseThrow(() -> new ProtocolException("Invalid transaction id"));
		BepStr ver = getClientVersion(dict).orElse(null);

		switch (type) {
			case QUERY:
				return fetchQueryMessage(dict, tid, ver);
			case RESPONSE:
				return fetchResponseMessage(dict, tid, ver);
			case ERROR:
				return fetchErrorMessage(dict, tid, ver);
			default:
				throw new IllegalStateException("Unknown type " + type.name());
		}
	}

	protected QueryMessage fetchQueryMessage(BepDict context, BepStr transaction, @Nullable BepStr version) {
		BepStr method = context.find(MessageKey.KEY_QUERY_METHOD, BepStr.class)
				.orElseThrow(() -> new ProtocolException("Miss query method"));
		BepDict arguments = context.find(MessageKey.KEY_QUERY_ARGV, BepDict.class)
				.orElseThrow(() -> new ProtocolException("Miss query arguments"));
		return new QueryMessage(transaction, version, method, arguments);
	}

	protected ResponseMessage fetchResponseMessage(BepDict context, BepStr transaction, @Nullable BepStr version) {
		BepDict body = context.find(MessageKey.KEY_RESPONSE_BODY, BepDict.class)
				.orElseThrow(() -> new ProtocolException("Miss response body"));
		return new ResponseMessage(transaction, version, body);
	}

	protected ErrorMessage fetchErrorMessage(BepDict context, BepStr transaction, @Nullable BepStr version) {
		final BepList body = context.find(MessageKey.KEY_ERROR_BODY, BepList.class)
				.orElseThrow(() -> new ProtocolException("Miss error body"));
		if (body.getContent().size() < ERROR_PARTS) {
			throw new ProtocolException("Invalid error body");
		}
		final BepInt code = Optional.of(body.getContent().get(0)).filter(o -> o instanceof BepInt).map(o -> (BepInt) o)
				.orElseThrow(() -> new ProtocolException("Miss error code"));

		final BepStr msg = Optional.of(body.getContent().get(1)).filter(o -> o instanceof BepStr).map(o -> (BepStr) o)
				.orElseThrow(() -> new ProtocolException("Miss error message"));

		return new ErrorMessage(transaction, version, code, msg);
	}

	protected Optional<MessageType> getMessageType(BepDict dict) {
		BepObject object = dict.get(MessageKey.KEY_TYPE);
		if (object == null) {
			log.debug("Miss message type");
			return Optional.empty();
		}
		if (object instanceof BepStr) {
			BepStr str = ((BepStr) object);
			Optional<MessageType> type = MessageType.parse(str.getByteAsChar(0));
			if (!type.isPresent()) {
				log.debug("Unknown message type: {}", str.asString());
			}
			return type;
		}
		else {
			log.debug("Message type not string,got " + object.getClass().getSimpleName());
			return Optional.empty();
		}
	}

	protected Optional<BepStr> getTransactionId(BepDict dict) {
		BepObject object = dict.get(MessageKey.KEY_TRANSACTION_ID);
		if (object == null) {
			log.debug("Miss transaction id");
			return Optional.empty();
		}
		if (object instanceof BepStr) {
			return Optional.of((BepStr) object);
		}
		else {
			log.debug("Message type not string,got " + object.getClass().getSimpleName());
			return Optional.empty();
		}
	}

	protected Optional<BepStr> getClientVersion(BepDict dict) {
		BepObject object = dict.get(MessageKey.KEY_VERSION);
		if (object == null) {
			return Optional.empty();
		}
		if (object instanceof BepStr) {
			return Optional.of((BepStr) object);
		}
		else {
			log.debug("Client version not string,got " + object.getClass().getSimpleName());
			return Optional.empty();
		}
	}

}
