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

package com.power4j.kit.bittorrent.peer.io;

import com.power4j.kit.bittorrent.common.Constant;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.dht.model.NodeId;
import com.power4j.kit.bittorrent.exceptions.ProtocolException;
import com.power4j.kit.bittorrent.peer.message.MessageId;
import com.power4j.kit.bittorrent.peer.PacketConstant;
import com.power4j.kit.bittorrent.peer.message.PeerHandshake;
import com.power4j.kit.bittorrent.peer.message.PeerMessage;
import org.apache.commons.codec.binary.Hex;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/20
 * @since 1.0
 */
public class PacketReader {

	public PeerHandshake readPeerHandshake(InputStream stream) throws IOException {
		byte[] buf = new byte[PacketConstant.PEER_HANDSHAKE_LENGTH];
		int len = stream.read(buf);
		if (len == Constant.EOF) {
			throw new EOFException();
		}
		if (!checkHandshakeHead(buf)) {
			throw new ProtocolException("Invalid head:" + Hex.encodeHexString(Arrays.copyOf(buf, 20)));
		}
		BepStr hash = BepStr.of(buf, 28, 20);
		NodeId peer = NodeId.of(buf, 48);
		return new PeerHandshake(hash, peer);
	}

	public PeerMessage readPeerMessage(InputStream stream) throws IOException {
		byte[] header = new byte[PacketConstant.PEER_MESSAGE_HEAD_LENGTH];
		int len = stream.read(header);
		if (len == Constant.EOF) {
			throw new EOFException();
		}
		final int id = header[PacketConstant.PEER_MESSAGE_HEAD_LENGTH - 1];
		MessageId messageId = MessageId.parseValue(id).orElseThrow(() -> new ProtocolException("Invalid msg id:" + id));

		int payload = readInt(header, 0) - PacketConstant.PEER_MESSAGE_HEAD_LENGTH;
		if (payload == 0) {
			return new PeerMessage(messageId, null);
		}
		if (payload < 0) {
			throw new IllegalStateException("Int overflow");
		}
		if (stream.available() < payload) {
			throw new ProtocolException("Payload not ready,required length:" + payload);
		}
		// FIXME: maybe huge memory usage
		byte[] bytes = new byte[payload];
		len = stream.read(bytes);
		assert len == payload;
		return new PeerMessage(messageId, bytes);
	}

	protected boolean checkHandshakeHead(byte[] input) {
		if (input.length < PacketConstant.PEER_HANDSHAKE_HEAD.length) {
			return false;
		}
		for (int i = 0; i < PacketConstant.PEER_HANDSHAKE_HEAD.length; ++i) {
			if (input[i] != PacketConstant.PEER_HANDSHAKE_HEAD[i]) {
				return false;
			}
		}
		return true;
	}

	public int readInt(byte[] buff, int offset) {
		ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).put(buff, offset, 4);
		buffer.flip();
		return buffer.getInt();
	}

}
