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
import com.power4j.kit.bittorrent.dht.ResponseBuilder;
import com.power4j.kit.bittorrent.dht.codec.MessageEncoder;
import com.power4j.kit.bittorrent.dht.message.ResponseMessage;
import com.power4j.kit.bittorrent.dht.model.NodeId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
class ResponseBuilderTest {

	private final MessageEncoder encoder = new MessageEncoder();

	private final BepStr tx = BepStr.of("aa");

	private final NodeId id = NodeId.of("abcdefghij0123456789");

	private final NodeId target = NodeId.of("mnopqrstuvwxyz123456");

	private final BepStr hash = BepStr.of("mnopqrstuvwxyz123456");

	private final BepStr token = BepStr.of("aoeusnth");

	private final ResponseBuilder helper = new ResponseBuilder();

	private ByteArrayOutputStream stream;

	@BeforeEach
	void setup() {
		stream = new ByteArrayOutputStream();
	}

	@Test
	void pingResponse() throws IOException {
		ResponseMessage message = helper.pingResponse(tx, target);
		encoder.encode(message, stream);
		Assertions.assertEquals("d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re", stream.toString());
	}

	@Test
	void findNodeResponse() throws IOException {
		ResponseMessage message = helper.findNodeResponse(tx, NodeId.of("0123456789abcdefghij"),
				Arrays.asList(id, target));
		encoder.encode(message, stream);
		Assertions.assertEquals(
				"d1:rd2:id20:0123456789abcdefghij5:nodes40:abcdefghij0123456789mnopqrstuvwxyz123456e1:t2:aa1:y1:re",
				stream.toString());
	}

	@Test
	void getPeersResponse() throws IOException {
		ResponseMessage message = helper.getPeersResponse(tx, id, token,
				Arrays.asList(BepStr.of("axje.u"), BepStr.of("idhtnm")));
		encoder.encode(message, stream);
		Assertions.assertEquals(
				"d1:rd2:id20:abcdefghij01234567895:token8:aoeusnth6:valuesl6:axje.u6:idhtnmee1:t2:aa1:y1:re",
				stream.toString());
	}

	@Test
	void noPeerResponse() throws IOException {
		ResponseMessage message = helper.noPeerResponse(tx, id, token, Arrays.asList(id, target));
		encoder.encode(message, stream);
		Assertions.assertEquals(
				"d1:rd2:id20:abcdefghij01234567895:nodes40:abcdefghij0123456789mnopqrstuvwxyz1234565:token8:aoeusnthe1:t2:aa1:y1:re",
				stream.toString());
	}

	@Test
	void announcePeerResponse() throws IOException {
		ResponseMessage message = helper.announcePeerResponse(tx, target);
		encoder.encode(message, stream);
		Assertions.assertEquals("d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re", stream.toString());
	}

}