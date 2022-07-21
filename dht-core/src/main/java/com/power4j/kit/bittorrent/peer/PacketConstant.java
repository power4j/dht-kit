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

package com.power4j.kit.bittorrent.peer;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/21
 * @since 1.0
 */
public interface PacketConstant {

	/** 19 + BitTorrent protocol */
	byte[] PEER_HANDSHAKE_HEAD = { 19, 66, 105, 116, 84, 111, 114, 114, 101, 110, 116, 32, 112, 114, 111, 116, 111, 99,
			111, 108 };

	byte[] PEER_HANDSHAKE_RESERVED = { 0, 0, 0, 0, 0, 0, 0, 0 };

	int PEER_HANDSHAKE_LENGTH = 68;

	/** 4 byte length + 1 byte message type */
	int PEER_MESSAGE_HEAD_LENGTH = 5;

	/** 4 byte length + 1 byte message type + 1 byte extend message type */
	int EXTEND_MESSAGE_HEAD_LENGTH = 6;

}
