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

import com.power4j.kit.bittorrent.core.BepInt;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.dht.codec.MessageDecoder;
import com.power4j.kit.bittorrent.dht.codec.MessageKey;
import com.power4j.kit.bittorrent.dht.message.Message;
import com.power4j.kit.bittorrent.dht.message.QueryMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
class MessageDecoderTest {

	private final MessageDecoder decoder = new MessageDecoder();

	@Test
	void decodePing() throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(
				"d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe".getBytes());
		Message message = decoder.decode(stream);
		Assertions.assertTrue(message instanceof QueryMessage);
		QueryMessage queryMessage = (QueryMessage) message;
		BepStr id = queryMessage.getArguments().find(MessageKey.KEY_ID, BepStr.class).orElse(null);
		Assertions.assertTrue(BepStr.of("abcdefghij0123456789").dataEquals(id));
	}

	@Test
	void decodeFindNode() throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(
				"d1:ad2:id20:abcdefghij01234567896:target20:mnopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe"
						.getBytes());
		Message message = decoder.decode(stream);
		Assertions.assertTrue(message instanceof QueryMessage);
		QueryMessage queryMessage = (QueryMessage) message;
		BepStr target = queryMessage.getArguments().find(MessageKey.KEY_TARGET, BepStr.class).orElse(null);
		Assertions.assertTrue(BepStr.of("mnopqrstuvwxyz123456").dataEquals(target));
	}

	@Test
	void decodeGetPeers() throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(
				"d1:ad2:id20:abcdefghij01234567899:info_hash20:mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe"
						.getBytes());
		Message message = decoder.decode(stream);
		Assertions.assertTrue(message instanceof QueryMessage);
		QueryMessage queryMessage = (QueryMessage) message;
		BepStr hash = queryMessage.getArguments().find(MessageKey.KEY_INFO_HASH, BepStr.class).orElse(null);
		Assertions.assertTrue(BepStr.of("mnopqrstuvwxyz123456").dataEquals(hash));
	}

	@Test
	void decodeAnnouncePeer() throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(
				"d1:ad2:id20:abcdefghij012345678912:implied_porti1e9:info_hash20:mnopqrstuvwxyz1234564:porti6881e5:token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe"
						.getBytes());
		Message message = decoder.decode(stream);
		Assertions.assertTrue(message instanceof QueryMessage);
		QueryMessage queryMessage = (QueryMessage) message;
		BepInt port = queryMessage.getArguments().find(MessageKey.KEY_PORT, BepInt.class).orElse(null);
		Assertions.assertNotNull(port);
		Assertions.assertEquals(6881, port.getValue().intValue());
	}

}