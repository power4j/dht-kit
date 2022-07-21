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
import com.power4j.kit.bittorrent.common.Constant;
import org.apache.commons.lang3.Validate;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public class NodeId {

	public final static int BYTES = 20;

	private final byte[] bytes;

	public static NodeId random(Random random) {
		byte[] bytes = new byte[BYTES];
		random.nextBytes(bytes);
		return new NodeId(bytes);
	}

	public static NodeId random() {
		return random(SecureRandomHolder.RANDOM);
	}

	public static NodeId fillWith(int value) {
		byte[] bytes = new byte[BYTES];
		for (int i = 0; i < BYTES; ++i) {
			bytes[i] = (byte) value;
		}
		return new NodeId(bytes);
	}

	public static NodeId of(byte[] data, int offset) {
		return new NodeId(Arrays.copyOfRange(data, offset, offset + BYTES));
	}

	public static NodeId of(byte[] bytes) {
		Validate.isTrue(bytes.length == BYTES);
		return of(bytes, 0);
	}

	public static NodeId of(String value) {
		Validate.isTrue(value.length() == BYTES);
		return new NodeId(value.getBytes(Constant.RAW_CHARSET));
	}

	public static NodeId of(BepStr str) {
		Validate.isTrue(str.getValue().length == BYTES);
		return new NodeId(str.getValue());
	}

	NodeId(byte[] bytes) {
		this.bytes = Arrays.copyOf(bytes, BYTES);
	}

	public byte[] getBytes() {
		return bytes;
	}

	public BepStr asBepStr() {
		return BepStr.of(bytes);
	}

	private static class SecureRandomHolder {

		static final SecureRandom RANDOM = new SecureRandom();

	}

}
