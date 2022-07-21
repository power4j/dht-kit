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

import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.dht.RequestBuilder;
import com.power4j.kit.bittorrent.dht.TransactionManager;
import com.power4j.kit.bittorrent.dht.codec.MessageEncoder;
import com.power4j.kit.bittorrent.dht.message.QueryMessage;
import com.power4j.kit.bittorrent.dht.model.NodeId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
class RequestBuilderTest {

	private final MessageEncoder encoder = new MessageEncoder();

	private final TransactionManager mockTm = () -> BepStr.of("aa");

	private final NodeId id = NodeId.of("abcdefghij0123456789");

	private final NodeId target = NodeId.of("mnopqrstuvwxyz123456");

	private final BepStr hash = BepStr.of("mnopqrstuvwxyz123456");

	private final RequestBuilder requestBuilder = new RequestBuilder(mockTm);

	private ByteArrayOutputStream stream;

	@BeforeEach
	void setup() {
		stream = new ByteArrayOutputStream();
	}

	@Test
	void ping() throws IOException {
		QueryMessage message = requestBuilder.ping(id);
		encoder.encode(message, stream);
		Assertions.assertEquals("d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe", stream.toString());
	}

	@Test
	void findNode() throws IOException {
		QueryMessage message = requestBuilder.findNode(id, target);
		encoder.encode(message, stream);
		Assertions.assertEquals(
				"d1:ad2:id20:abcdefghij01234567896:target20:mnopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe",
				stream.toString());
	}

	@Test
	void getPeers() throws IOException {
		QueryMessage message = requestBuilder.getPeers(id, hash);
		encoder.encode(message, stream);
		Assertions.assertEquals(
				"d1:ad2:id20:abcdefghij01234567899:info_hash20:mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe",
				stream.toString());
	}

	@Test
	void announcePeer() throws IOException {
		QueryMessage message = requestBuilder.announcePeer(id, hash, 6881, true, BepStr.of("aoeusnth"));
		encoder.encode(message, stream);
		Assertions.assertEquals(
				"d1:ad2:id20:abcdefghij012345678912:implied_porti1e9:info_hash20:mnopqrstuvwxyz1234564:porti6881e5:token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe",
				stream.toString());
	}

}