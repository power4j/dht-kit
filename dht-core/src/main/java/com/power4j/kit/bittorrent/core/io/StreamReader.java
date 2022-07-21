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

package com.power4j.kit.bittorrent.core.io;

import com.power4j.kit.bittorrent.common.Constant;
import com.power4j.kit.bittorrent.core.BepDict;
import com.power4j.kit.bittorrent.core.BepInt;
import com.power4j.kit.bittorrent.core.BepList;
import com.power4j.kit.bittorrent.core.BepObject;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.core.ChunkType;
import com.power4j.kit.bittorrent.exceptions.InvalidChunkException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;

import static com.power4j.kit.bittorrent.core.BepObject.FLAG_COLON;
import static com.power4j.kit.bittorrent.core.BepObject.FLAG_END;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/14
 * @since 1.0
 */
@Slf4j
public class StreamReader {

	public final static Charset DEFAULT_CHARSET = Constant.DEFAULT_TEXT_CHARSET;

	/**
	 * for text
	 */
	private Charset charset;

	private final PushbackInputStream in;

	public static StreamReader of(String context, Charset charset) {
		return of(context.getBytes(Constant.RAW_CHARSET), charset);
	}

	public static StreamReader of(String context) {
		return of(context, DEFAULT_CHARSET);
	}

	public static StreamReader of(byte[] bytes, int offset, int length, Charset charset) {
		return of(new ByteArrayInputStream(bytes, offset, length), charset);
	}

	public static StreamReader of(byte[] bytes, Charset charset) {
		return of(new ByteArrayInputStream(bytes), charset);
	}

	public static StreamReader of(byte[] bytes) {
		return of(bytes, DEFAULT_CHARSET);
	}

	public static StreamReader of(InputStream stream, Charset charset) {
		return new StreamReader(stream, charset);
	}

	public static StreamReader of(InputStream stream) {
		return of(stream, DEFAULT_CHARSET);
	}

	public StreamReader(InputStream in, Charset charset) {
		this.in = new PushbackInputStream(in);
		this.charset = charset;
	}

	public StreamReader(InputStream in) {
		this(in, DEFAULT_CHARSET);
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * 读取字符串
	 * @return BepStr
	 * @throws IOException IO错误
	 * @throws InvalidChunkException 无效数据
	 */
	public BepStr readStr() throws IOException {
		byte[] bytes = readStrChunk();
		assert bytes != null;
		return BepStr.of(bytes);
	}

	/**
	 * 读取整数
	 * @return BepInt
	 * @throws IOException IO错误
	 * @throws InvalidChunkException 无效数据
	 */
	public BepInt readInt() throws IOException {
		checkNextToken(ChunkType.INT);
		skipOne();
		StringBuilder builder = new StringBuilder(8);
		int val;
		while ((val = in.read()) != FLAG_END) {
			checkNotEof(val);
			builder.append((char) val);
		}
		return new BepInt(parseInt(builder.toString()));
	}

	/**
	 * 读取列表类型
	 * @return
	 * @throws IOException IO错误
	 * @throws InvalidChunkException 无效数据
	 */
	public BepList readList() throws IOException {
		checkNextToken(ChunkType.LIST);
		skipOne();
		BepList list = new BepList();
		int val;
		while ((val = in.read()) != FLAG_END) {
			checkNotEof(val);
			in.unread(val);
			list.add(readAny());
		}
		return list;
	}

	/**
	 * 读取字典类型
	 * @return BepDict
	 * @throws IOException IO错误
	 * @throws InvalidChunkException 无效数据
	 */
	public BepDict readDict() throws IOException {
		checkNextToken(ChunkType.DICT);
		skipOne();
		BepDict dict = new BepDict();
		int val;
		while ((val = in.read()) != FLAG_END) {
			checkNotEof(val);
			in.unread(val);
			dict.put(readStr().asString(charset), readAny());
		}
		return dict;
	}

	/**
	 * 读取任意类型
	 * @return BepObject
	 * @throws IOException IO错误
	 * @throws InvalidChunkException 无效数据
	 */
	public BepObject readAny() throws IOException {
		int token = peek();
		checkNotEof(token);
		if (BepObject.FLAG_DICT == token) {
			return readDict();
		}
		if (BepObject.FLAG_LIST == token) {
			return readList();
		}
		if (BepObject.FLAG_INT == token) {
			return readInt();
		}
		if (token >= Constant.CHAR_ZERO && token <= Constant.CHAR_NINE) {
			return readStr();
		}
		throw new InvalidChunkException("Unexpected token '" + new String(Character.toChars(token)) + "'");
	}

	byte[] readStrChunk() throws IOException {
		checkNextToken(ChunkType.STR);
		StringBuilder builder = new StringBuilder(8);
		int val;
		while ((val = in.read()) != FLAG_COLON) {
			checkNotEof(val);
			builder.append((char) val);
		}
		int length = parseLength(builder.toString());
		assert length >= 0;
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; ++i) {
			val = in.read();
			checkNotEof(val);
			bytes[i] = (byte) val;
		}
		return bytes;
	}

	protected void skip(long size) throws IOException {
		long actual = in.skip(size);
		if (actual != size) {
			log.debug("skip {} bytes,actual : {}", size, actual);
		}
	}

	protected void skipOne() throws IOException {
		skip(1);
	}

	/**
	 * Peek next byte
	 * @return the next byte of data, or -1 if the end of the stream has been reached.
	 * @throws IOException
	 */
	protected int peek() throws IOException {
		int value = in.read();
		in.unread(value);
		return value;
	}

	protected void checkToken(int value, ChunkType excepted) {
		// @formatter:off
		ChunkType.fromToken(value)
				.filter(excepted::equals)
				.orElseThrow(() ->
						new InvalidChunkException("Unexpected token '" + new String(Character.toChars(value))+ "'")
				);
		// @formatter:on
	}

	protected void checkNextToken(ChunkType excepted) throws IOException {
		checkToken(peek(), excepted);
	}

	protected void checkNotEof(int value) throws EOFException {
		if (Constant.EOF == value) {
			throw new EOFException();
		}
	}

	int parseLength(String str) {
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			throw new InvalidChunkException("Invalid length:'" + str + "'");
		}
	}

	BigInteger parseInt(String str) {
		try {
			return new BigInteger(str);
		}
		catch (NumberFormatException e) {
			throw new InvalidChunkException("Invalid number:'" + str + "'");
		}
	}

}
