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

package com.power4j.kit.bittorrent.core;

import com.power4j.kit.bittorrent.dht.model.NodeId;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.lang.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/6/20
 * @since 1.0
 */
public class BepStr extends ValueObject<byte[]> {

	private final byte[] data;

	public static BepStr ofChar(int c) {
		byte[] bytes = new byte[1];
		bytes[0] = (byte) c;
		return new BepStr(bytes);
	}

	public static BepStr ofHex(String hex) {
		try {
			return new BepStr(Hex.decodeHex(hex));
		}
		catch (DecoderException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static BepStr of(byte[] data, int offset, int length) {
		return new BepStr(Arrays.copyOfRange(data, offset, offset + length));
	}

	public static BepStr of(byte[] data) {
		return of(data, 0, data.length);
	}

	public static BepStr of(String str, Charset charset) {
		return of(str.getBytes(charset));
	}

	public static BepStr of(String str) {
		return of(str, StandardCharsets.UTF_8);
	}

	public static BepStr compact(Collection<BepStr> list) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream(NodeId.BYTES * list.size());
		try {
			for (BepStr o : list) {
				stream.write(o.getValue());
			}
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return BepStr.of(stream.toByteArray());
	}

	public static BepStr compact(BepStr... str) {
		return compact(Arrays.asList(str));
	}

	BepStr(byte[] data) {
		this.data = Arrays.copyOf(data, data.length);
	}

	@Override
	public ChunkType getType() {
		return ChunkType.STR;
	}

	@Override
	public byte[] getValue() {
		return data;
	}

	public byte getByte(int index) {
		return data[index];
	}

	public char getByteAsChar(int index) {
		return (char) getByte(index);
	}

	public String asString(Charset charset) {
		return new String(data, charset);
	}

	public String asString() {
		return asString(StandardCharsets.UTF_8);
	}

	public String getHexValue() {
		return Hex.encodeHexString(data);
	}

	public boolean dataEquals(@Nullable BepStr other) {
		if (other == null) {
			return false;
		}
		return Arrays.equals(data, other.data);
	}

	@Override
	public String toString() {
		return asString(StandardCharsets.ISO_8859_1);
	}

}
