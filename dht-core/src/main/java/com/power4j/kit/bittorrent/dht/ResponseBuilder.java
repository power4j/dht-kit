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

import com.power4j.kit.bittorrent.core.BepDict;
import com.power4j.kit.bittorrent.core.BepList;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.dht.codec.MessageKey;
import com.power4j.kit.bittorrent.dht.message.ResponseMessage;
import com.power4j.kit.bittorrent.dht.model.NodeId;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public class ResponseBuilder {

	@Nullable
	private BepStr clientVersion;

	@Nullable
	public BepStr getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(@Nullable BepStr clientVersion) {
		this.clientVersion = clientVersion;
	}

	/**
	 * Ping响应
	 * @param transactionId 请求的事务ID
	 * @param nodeId 请求的 Node ID
	 * @return ResponseMessage
	 */
	public ResponseMessage pingResponse(BepStr transactionId, NodeId nodeId) {
		BepDict body = BepDict.of(MessageKey.KEY_ID, nodeId.asBepStr());
		return new ResponseMessage(transactionId, getClientVersion(), body);
	}

	/**
	 * Find node 响应
	 * @param transactionId 请求的事务ID
	 * @param nodeId 请求的 Node ID
	 * @param nodes 返回的Node ID
	 * @return ResponseMessage
	 */
	public ResponseMessage findNodeResponse(BepStr transactionId, NodeId nodeId, List<NodeId> nodes) {
		List<BepStr> list = nodes.stream().map(NodeId::asBepStr).collect(Collectors.toList());
		BepDict body = BepDict.of(MessageKey.KEY_NODES, BepStr.compact(list));
		body.put(MessageKey.KEY_ID, nodeId.asBepStr());
		return new ResponseMessage(transactionId, getClientVersion(), body);
	}

	/**
	 * Get Peers 响应
	 * @param transactionId 请求的事务ID
	 * @param nodeId 请求的 Node ID
	 * @param token 返回的token
	 * @param peers 返回的peer
	 * @return ResponseMessage
	 */
	public ResponseMessage getPeersResponse(BepStr transactionId, NodeId nodeId, BepStr token, List<BepStr> peers) {
		BepDict body = BepDict.of(MessageKey.KEY_VALUES, new BepList(peers));
		body.put(MessageKey.KEY_TOKEN, token);
		body.put(MessageKey.KEY_ID, nodeId.asBepStr());
		return new ResponseMessage(transactionId, getClientVersion(), body);
	}

	/**
	 * Get Peers 响应
	 * @param transactionId 请求的事务ID
	 * @param nodeId 请求的 Node ID
	 * @param token 返回的token
	 * @param nodes 返回的Node ID
	 * @return ResponseMessage
	 */
	public ResponseMessage noPeerResponse(BepStr transactionId, NodeId nodeId, BepStr token, List<NodeId> nodes) {
		List<BepStr> list = nodes.stream().map(NodeId::asBepStr).collect(Collectors.toList());
		BepDict body = BepDict.of(MessageKey.KEY_NODES, BepStr.compact(list));
		body.put(MessageKey.KEY_TOKEN, token);
		body.put(MessageKey.KEY_ID, nodeId.asBepStr());
		return new ResponseMessage(transactionId, getClientVersion(), body);
	}

	/**
	 * Announce peer 响应
	 * @param transactionId 请求的事务ID
	 * @param nodeId 请求的 Node ID
	 * @return ResponseMessage
	 */
	public ResponseMessage announcePeerResponse(BepStr transactionId, NodeId nodeId) {
		BepDict body = BepDict.of(MessageKey.KEY_ID, nodeId.asBepStr());
		return new ResponseMessage(transactionId, getClientVersion(), body);
	}

}
