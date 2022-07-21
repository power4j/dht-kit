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
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/19
 * @since 1.0
 */
@Slf4j
@UtilityClass
public class DhtUtil {

	public static final int IP4_BYTES = 4;

	public static final int PORT_BYTES = 2;

	public static final int PEER_CONTRACT_BYTES = IP4_BYTES + PORT_BYTES;

	public static final int NODE_CONTRACT_BYTES = NodeId.BYTES + PEER_CONTRACT_BYTES;

	public ByteBuffer bufferBe(int size) {
		return ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
	}

	public int readUnsignedShort(byte[] buff, int offset) {
		ByteBuffer buffer = bufferBe(2).put(buff, offset, 2);
		buffer.flip();
		return Short.toUnsignedInt(buffer.getShort());
	}

	public BepStr compactPeerContract(Inet4Address address, int port) {
		// @formatter:off
		byte[] bytes = bufferBe(PEER_CONTRACT_BYTES)
				.put(address.getAddress())
				.putShort((short)port)
				.array();
		// @formatter:on
		return BepStr.of(bytes);
	}

	public BepStr compactNodeContract(NodeId nodeId, Inet4Address address, int port) {
		// @formatter:off
		byte[] bytes = bufferBe(NODE_CONTRACT_BYTES)
				.put(nodeId.getBytes())
				.put(address.getAddress())
				.putShort((short)port)
				.array();
		// @formatter:on
		return BepStr.of(bytes);
	}

	public List<PeerContact> extractPeerContract(BepStr compacted) {
		byte[] bytes = compacted.getValue();
		List<PeerContact> list = new ArrayList<>(8);
		for (int i = 0; i < bytes.length;) {
			if (i + PEER_CONTRACT_BYTES > bytes.length) {
				break;
			}
			try {
				Inet4Address address = (Inet4Address) InetAddress
						.getByAddress(Arrays.copyOfRange(bytes, i, i + IP4_BYTES));
				i += IP4_BYTES;
				int port = readUnsignedShort(bytes, i);
				i += PORT_BYTES;
				list.add(PeerContact.of(address, port));
			}
			catch (UnknownHostException e) {
				throw new IllegalStateException(e);
			}
		}
		return list;
	}

	public List<NodeContract> extractNodeContract(BepStr compacted) {
		byte[] bytes = compacted.getValue();
		List<NodeContract> list = new ArrayList<>(8);
		for (int i = 0; i < bytes.length;) {
			if (i + NODE_CONTRACT_BYTES > bytes.length) {
				break;
			}
			try {
				NodeId nodeId = NodeId.of(Arrays.copyOfRange(bytes, i, i + NodeId.BYTES));
				i += NodeId.BYTES;
				Inet4Address address = (Inet4Address) InetAddress
						.getByAddress(Arrays.copyOfRange(bytes, i, i + IP4_BYTES));
				i += IP4_BYTES;
				int port = readUnsignedShort(bytes, i);
				i += PORT_BYTES;
				list.add(NodeContract.of(nodeId, address, port));
			}
			catch (UnknownHostException e) {
				throw new IllegalStateException(e);
			}
		}
		return list;
	}

}
