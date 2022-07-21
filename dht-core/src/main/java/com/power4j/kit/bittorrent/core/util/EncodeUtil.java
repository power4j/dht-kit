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

package com.power4j.kit.bittorrent.core.util;

import com.power4j.kit.bittorrent.common.Constant;
import com.power4j.kit.bittorrent.core.BepDict;
import com.power4j.kit.bittorrent.core.BepInt;
import com.power4j.kit.bittorrent.core.BepList;
import com.power4j.kit.bittorrent.core.BepObject;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.core.io.StreamReader;
import com.power4j.kit.bittorrent.core.io.StreamWriter;
import com.power4j.kit.bittorrent.exceptions.EncodeException;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/20
 * @since 1.0
 */
@UtilityClass
public class EncodeUtil {

	// ~ Encoding
	// ===================================================================================================

	public void encode(BepObject object, OutputStream outputStream, Charset charset) {
		try {
			StreamWriter.of(outputStream, charset).write(object);
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	public void encode(BepObject object, OutputStream outputStream) {
		encode(object, outputStream, Constant.DEFAULT_TEXT_CHARSET);
	}

	public String encodeToString(BepObject object) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream(64);
		encode(object, stream, Constant.DEFAULT_TEXT_CHARSET);
		return new String(stream.toByteArray(), Constant.DEFAULT_TEXT_CHARSET);
	}

	// ~ Read Object
	// ===================================================================================================

	public BepObject readObject(InputStream stream, Charset charset) {
		try {
			return StreamReader.of(stream, charset).readAny();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	public BepObject readObject(InputStream stream) {
		try {
			return StreamReader.of(stream).readAny();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	// ~ Read Integer
	// ===================================================================================================

	public BepInt readInt(InputStream stream) {
		try {
			return StreamReader.of(stream).readInt();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	// ~ Read Str
	// ===================================================================================================

	public BepStr readStr(InputStream stream, Charset charset) {
		try {
			return StreamReader.of(stream, charset).readStr();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	public BepStr readStr(InputStream stream) {
		try {
			return StreamReader.of(stream).readStr();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	// ~ Read List
	// ===================================================================================================

	public BepList readList(InputStream stream, Charset charset) {
		try {
			return StreamReader.of(stream, charset).readList();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	public BepList readList(InputStream stream) {
		try {
			return StreamReader.of(stream).readList();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	// ~ Read Dict
	// ===================================================================================================

	public BepDict readDict(InputStream stream, Charset charset) {
		try {
			return StreamReader.of(stream, charset).readDict();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	public BepDict readDict(InputStream stream) {
		try {
			return StreamReader.of(stream).readDict();
		}
		catch (IOException e) {
			throw new EncodeException(e);
		}
	}

}
