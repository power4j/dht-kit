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

package com.power4j.kit.bittorrent.dht.model;

import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.dht.util.DhtUtil;
import lombok.Getter;

import java.net.Inet4Address;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/19
 * @since 1.0
 */
@Getter
public class NodeContract {

	private final NodeId nodeId;

	private final Inet4Address address;

	private final int port;

	public static NodeContract of(NodeId nodeId, Inet4Address address, int port) {
		return new NodeContract(nodeId, address, port);
	}

	NodeContract(NodeId nodeId, Inet4Address address, int port) {
		this.nodeId = nodeId;
		this.address = address;
		this.port = port;
	}

	public BepStr compact() {
		return DhtUtil.compactNodeContract(nodeId, address, port);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(nodeId.asBepStr().getHexValue());
		builder.append("] ");
		builder.append(PeerContact.of(address, port));
		return builder.toString();
	}

}
