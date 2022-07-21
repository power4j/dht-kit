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

package com.power4j.bittorrent;

import com.power4j.kit.bittorrent.core.BepList;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.dht.model.NodeContract;
import com.power4j.kit.bittorrent.dht.model.NodeId;
import com.power4j.kit.bittorrent.dht.RequestBuilder;
import com.power4j.kit.bittorrent.dht.codec.MessageDecoder;
import com.power4j.kit.bittorrent.dht.codec.MessageEncoder;
import com.power4j.kit.bittorrent.dht.codec.MessageKey;
import com.power4j.kit.bittorrent.dht.message.ErrorMessage;
import com.power4j.kit.bittorrent.dht.message.Message;
import com.power4j.kit.bittorrent.dht.message.QueryMessage;
import com.power4j.kit.bittorrent.dht.message.ResponseMessage;
import com.power4j.kit.bittorrent.dht.util.DhtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;
import reactor.netty.resources.LoopResources;
import reactor.netty.udp.UdpClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/19
 * @since 1.0
 */
@Slf4j
public class Client {

	private final int MAX_CONNECTIONS = 2500;

	private final MessageDecoder decoder = new MessageDecoder();

	private final RequestBuilder requestBuilder = new RequestBuilder();

	private final AtomicLong connCount = new AtomicLong();

	private final BepStr hash = BepStr.ofHex("353c26a56f2b6e9e7a9cf7770a55b698973b221d");

	private final NodeId self = NodeId.random();

	private final NodeId target = NodeId.random();

	private final LoopResources loop;

	@Nullable
	private final InetAddress bindingAddress;

	private final int bingingPort;

	public Client(LoopResources loop, @Nullable InetAddress bindingAddress, int bingingPort) {
		this.loop = loop;
		this.bindingAddress = bindingAddress;
		this.bingingPort = bingingPort;
	}

	public Connection run(String host, int port) {

		Connection connection = UdpClient.create().bindAddress(() -> new InetSocketAddress(bindingAddress, bingingPort))
				.wiretap(true).runOn(loop).host(host).port(port).connectNow(Duration.ofSeconds(30));

		QueryMessage findNodeMsg = requestBuilder.findNode(self, target);

		connection.outbound().sendByteArray(Mono.just(MessageEncoder.toBytes(findNodeMsg))).then().subscribe();
		// @formatter:off
		readResponse(connection.inbound())
				.map(msg -> {
					log.debug("response: {}", MessageEncoder.toString(msg));
					return msg.getBody().find(MessageKey.KEY_NODES, BepStr.class).map(s -> {
						List<NodeContract> list = DhtUtil.extractNodeContract(s);
						list.forEach(contract -> {
							log.info("Find node return node: {}", contract.toString());
						});
						return list;
					}).orElseGet(Collections::emptyList);
				})
				.filter(l -> !l.isEmpty())
				.next()
				.flatMapMany(Flux::fromIterable)
				.flatMap(this::getPeer)
				.then()
				.subscribe();
		// @formatter:on
		return connection;
	}

	private Mono<? extends Connection> connectTo(String host, int port, boolean wiretap) {
		if (connCount.get() >= MAX_CONNECTIONS) {
			log.info("Max connection limited({}),skip connect to {}:{}", MAX_CONNECTIONS, host, port);
			return Mono.empty();
		}
		// @formatter:off
		return UdpClient.create()
				.wiretap(wiretap)
				.runOn(loop)
				.host(host)
				.port(port)
				.connect()
				.doOnNext(c -> connCount.incrementAndGet());
		// @formatter:on
	}

	private Flux<Message> readMessage(NettyInbound inbound) {
		return inbound.receive().asInputStream().concatMap(in -> {
			Message msg = decoder.decode(in);
			logging(msg);
			return Mono.just(msg);
		});
	}

	private void logging(Message msg) {
		final String tx = msg.getTransaction().getHexValue();
		if (msg instanceof QueryMessage) {
			QueryMessage queryMessage = (QueryMessage) msg;
			log.debug("Query Message : id={},method={},arg={}", tx, queryMessage.getMethod().asString(),
					queryMessage.getArguments().getContent());
		}
		if (msg instanceof ResponseMessage) {
			ResponseMessage responseMessage = (ResponseMessage) msg;
			log.debug("Response Message :id={}", tx);
		}
		if (msg instanceof ErrorMessage) {
			ErrorMessage errorMessage = (ErrorMessage) msg;
			log.debug("Error Message :id={},code={},msg={}", tx, errorMessage.getCode().asInt(),
					errorMessage.getMessage().asString());
		}
	}

	private Flux<ResponseMessage> readResponse(NettyInbound inbound) {
		// @formatter:off
		return readMessage(inbound)
				.filter(m -> m instanceof ResponseMessage)
				.map(o ->  (ResponseMessage)o);
		// @formatter:on
	}

	private Mono<Void> getPeer(NodeContract contract) {
		Mono<byte[]> output = Mono.defer(() -> {
			QueryMessage getPeerMsg = requestBuilder.getPeers(self, hash);
			return Mono.just(MessageEncoder.toBytes(getPeerMsg));
		});

		log.debug("get peer from {}", contract);
		// @formatter:off
		return connectTo(contract.getAddress().getHostAddress(),contract.getPort(),true)
				.flatMap(connection -> connection
						.outbound()
						.sendByteArray(output)
						.then(readGetPeer(connection)
								.then(Mono.defer(() -> {
									connection.dispose();
									connCount.decrementAndGet();
									return Mono.empty();
								}))
						).then())
				.then()
				.doOnError(e -> log.error("error: {},conn = {}",e.getMessage(), connCount.get()));
		// @formatter:on
	}

	private Flux<ResponseMessage> readGetPeer(Connection connection) {
		// @formatter:off
		return readResponse(connection.inbound()).doOnNext(r -> {
			r.getBody().find(MessageKey.KEY_VALUES, BepList.class).ifPresent(l ->
					l.getContent().forEach(o -> {
						if (o instanceof BepStr) {
							DhtUtil.extractPeerContract((BepStr) o)
									.forEach(c -> log.info("Get peer return peer : {}", c.toString()));
						}
					}));
			r.getBody().find(MessageKey.KEY_NODES, BepStr.class).ifPresent(s -> {
				List<NodeContract> list = DhtUtil.extractNodeContract(s);
				list.forEach(contract -> log.info("Get peer return node {}", contract.toString()));
				list.forEach(contract -> CompletableFuture.runAsync(() -> getPeer(contract).subscribe()));
			});
		});
		// @formatter:on
	}

}
