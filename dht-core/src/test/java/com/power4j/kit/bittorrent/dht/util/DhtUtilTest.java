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

package com.power4j.kit.bittorrent.dht.util;

import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.dht.model.NodeContract;
import com.power4j.kit.bittorrent.dht.model.NodeId;
import com.power4j.kit.bittorrent.dht.model.PeerContact;
import com.power4j.kit.bittorrent.dht.util.DhtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/19
 * @since 1.0
 */
class DhtUtilTest {

	@Test
	void compactPeerContract() throws UnknownHostException {
		BepStr value = DhtUtil.compactPeerContract((Inet4Address) InetAddress.getByName("127.0.0.1"), 0x1234);
		Assertions.assertEquals(DhtUtil.PEER_CONTRACT_BYTES, value.getValue().length);
		Assertions.assertEquals(127, value.getValue()[0]);
		Assertions.assertEquals(0, value.getValue()[1]);
		Assertions.assertEquals(0, value.getValue()[2]);
		Assertions.assertEquals(1, value.getValue()[3]);
		Assertions.assertEquals(0x12, value.getValue()[4]);
		Assertions.assertEquals(0x34, value.getValue()[5]);
	}

	@Test
	void compactNodeContract() throws UnknownHostException {
		NodeId id = NodeId.fillWith(1);
		BepStr value = DhtUtil.compactNodeContract(id, (Inet4Address) InetAddress.getByName("127.0.0.1"), 0x1234);
		Assertions.assertEquals(DhtUtil.NODE_CONTRACT_BYTES, value.getValue().length);

		Assertions.assertEquals(1, value.getValue()[0]);
		Assertions.assertEquals(1, value.getValue()[9]);
		Assertions.assertEquals(1, value.getValue()[19]);

		Assertions.assertEquals(127, value.getValue()[20]);
		Assertions.assertEquals(0, value.getValue()[21]);
		Assertions.assertEquals(0, value.getValue()[22]);
		Assertions.assertEquals(1, value.getValue()[23]);
		Assertions.assertEquals(0x12, value.getValue()[24]);
		Assertions.assertEquals(0x34, value.getValue()[25]);
	}

	@Test
	void extractPeerContract() {
		byte[] input = { 127, 0, 0, 1, 0x12, 0x34, 127, 0, 0, 1, 0x12, 0x34 };
		List<PeerContact> contacts = DhtUtil.extractPeerContract(BepStr.of(input));
		Assertions.assertEquals(2, contacts.size());
		PeerContact contact = contacts.get(1);

		Assertions.assertEquals(127, contact.getAddress().getAddress()[0]);
		Assertions.assertEquals(0, contact.getAddress().getAddress()[1]);
		Assertions.assertEquals(0, contact.getAddress().getAddress()[2]);
		Assertions.assertEquals(1, contact.getAddress().getAddress()[3]);
		Assertions.assertEquals(0x1234, contact.getPort());
	}

	@Test
	void extractNodeContract() {
		byte[] input = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 127, 0, 0, 1, 0x12, 0x34 };
		List<NodeContract> contacts = DhtUtil.extractNodeContract(BepStr.of(input));
		Assertions.assertEquals(1, contacts.size());
		NodeContract contact = contacts.get(0);

		Assertions.assertEquals(1, contact.getNodeId().getBytes()[0]);
		Assertions.assertEquals(1, contact.getNodeId().getBytes()[7]);
		Assertions.assertEquals(1, contact.getNodeId().getBytes()[19]);

		Assertions.assertEquals(127, contact.getAddress().getAddress()[0]);
		Assertions.assertEquals(0, contact.getAddress().getAddress()[1]);
		Assertions.assertEquals(0, contact.getAddress().getAddress()[2]);
		Assertions.assertEquals(1, contact.getAddress().getAddress()[3]);
		Assertions.assertEquals(0x1234, contact.getPort());
	}

}