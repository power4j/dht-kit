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
import com.power4j.kit.bittorrent.core.BepInt;
import com.power4j.kit.bittorrent.core.BepObject;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.dht.codec.MessageKey;
import com.power4j.kit.bittorrent.dht.message.QueryMessage;
import com.power4j.kit.bittorrent.dht.model.NodeId;
import org.apache.commons.lang3.Validate;
import org.springframework.lang.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public class RequestBuilder {

	private final TransactionManager tm;

	@Nullable
	private BepStr clientVersion;

	public RequestBuilder(TransactionManager tm) {
		this.tm = tm;
	}

	public RequestBuilder() {
		this(new DefaultTransactionManager());
	}

	@Nullable
	public BepStr getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(@Nullable BepStr clientVersion) {
		this.clientVersion = clientVersion;
	}

	public QueryMessage ping(NodeId nodeId) {
		// @formatter:off
		return new QueryBuilder(BepStr.of(QueryMethod.PING.getValue()))
				.transaction(tm.next())
				.version(getClientVersion())
				.arg(MessageKey.KEY_ID,BepStr.of(nodeId.getBytes()))
				.build();
		// @formatter:on
	}

	public QueryMessage findNode(NodeId id, NodeId target) {
		// @formatter:off
		return new QueryBuilder(BepStr.of(QueryMethod.FIND_NODE.getValue()))
				.transaction(tm.next())
				.version(getClientVersion())
				.arg(MessageKey.KEY_ID,BepStr.of(id.getBytes()))
				.arg(MessageKey.KEY_TARGET,BepStr.of(target.getBytes()))
				.build();
		// @formatter:on
	}

	public QueryMessage getPeers(NodeId nodeId, BepStr hash) {
		// @formatter:off
		return new QueryBuilder(BepStr.of(QueryMethod.GET_PEERS.getValue()))
				.transaction(tm.next())
				.version(getClientVersion())
				.arg(MessageKey.KEY_ID,BepStr.of(nodeId.getBytes()))
				.arg(MessageKey.KEY_INFO_HASH,hash)
				.build();
		// @formatter:on
	}

	public QueryMessage announcePeer(NodeId nodeId, BepStr hash, int port, boolean impliedPort, BepStr token) {
		// @formatter:off
		return new QueryBuilder(BepStr.of(QueryMethod.ANNOUNCE_PEER.getValue()))
				.transaction(tm.next())
				.version(getClientVersion())
				.arg(MessageKey.KEY_ID,BepStr.of(nodeId.getBytes()))
				.arg(MessageKey.KEY_INFO_HASH,hash)
				.arg(MessageKey.KEY_PORT, BepInt.of(port))
				.arg(MessageKey.KEY_IMPLIED_PORT,BepInt.of(impliedPort?1:0))
				.arg(MessageKey.KEY_TOKEN,token)
				.build();
		// @formatter:on
	}

	static class QueryBuilder {

		@Nullable
		private BepStr transaction;

		@Nullable
		private BepStr version;

		private final BepStr method;

		private final BepDict.Builder argBuilder = BepDict.builder();

		QueryBuilder(BepStr method) {
			this.method = method;
		}

		public QueryBuilder transaction(BepStr value) {
			this.transaction = value;
			return this;
		}

		public QueryBuilder version(@Nullable BepStr value) {
			this.version = value;
			return this;
		}

		public QueryBuilder arg(String key, BepObject value) {
			argBuilder.entry(key, value);
			return this;
		}

		public QueryBuilder arg(String key, String value, Charset charset) {
			return arg(key, BepStr.of(value, charset));
		}

		public QueryBuilder arg(String key, String value) {
			return arg(key, BepStr.of(value, StandardCharsets.UTF_8));
		}

		public QueryBuilder arg(String key, long value) {
			return arg(key, BepInt.of(value));
		}

		public QueryMessage build() {
			Validate.notNull(transaction);
			Validate.notNull(method);
			return new QueryMessage(transaction, version, method, argBuilder.build());
		}

	}

}
