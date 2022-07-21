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
import com.power4j.kit.bittorrent.peer.message.ExtendMessage;
import com.power4j.kit.bittorrent.peer.PacketConstant;
import com.power4j.kit.bittorrent.peer.message.PeerHandshake;
import com.power4j.kit.bittorrent.peer.message.PeerMessage;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/20
 * @since 1.0
 */
public class PacketWriter {

	public void write(PeerHandshake object, OutputStream stream) throws IOException {
		Validate.isTrue(object.getHash().getValue().length == Constant.LENGTH_INFO_HASH);
		stream.write(PacketConstant.PEER_HANDSHAKE_HEAD);
		stream.write(PacketConstant.PEER_HANDSHAKE_RESERVED);
		stream.write(object.getHash().getValue());
		stream.write(object.getPeer().getBytes());
	}

	public void write(PeerMessage object, OutputStream stream) throws IOException {
		final int payload = object.payloadSize();
		stream.write(payload + PacketConstant.PEER_MESSAGE_HEAD_LENGTH);
		stream.write((byte) object.getId().getValue());
		if (payload > 0) {
			stream.write(object.requirePayload());
		}
	}

	public void write(ExtendMessage object, OutputStream stream) throws IOException {
		final int payload = object.payloadSize();
		stream.write(payload + PacketConstant.EXTEND_MESSAGE_HEAD_LENGTH);
		stream.write((byte) object.getId().getValue());
		if (payload > 0) {
			stream.write(object.requirePayload());
		}
	}

}
