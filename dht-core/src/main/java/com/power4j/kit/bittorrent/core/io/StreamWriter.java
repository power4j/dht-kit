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

import com.power4j.kit.bittorrent.core.BepDict;
import com.power4j.kit.bittorrent.core.BepInt;
import com.power4j.kit.bittorrent.core.BepList;
import com.power4j.kit.bittorrent.core.BepObject;
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.common.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public class StreamWriter {

	public final static Charset DEFAULT_CHARSET = Constant.DEFAULT_TEXT_CHARSET;

	private final OutputStream outputStream;

	private final Charset charset;

	public static StreamWriter of(OutputStream outputStream, Charset charset) {
		return new StreamWriter(outputStream, charset);
	}

	public static StreamWriter of(OutputStream outputStream) {
		return of(outputStream, DEFAULT_CHARSET);
	}

	StreamWriter(OutputStream outputStream, Charset charset) {
		this.outputStream = outputStream;
		this.charset = charset;
	}

	/**
	 * 编码并写入
	 * @param object BepObject
	 * @throws IOException IO错误
	 */
	public void write(BepObject object) throws IOException {
		writeAny(object);
	}

	protected void writeAny(BepObject object) throws IOException {
		switch (object.getType()) {
			case STR:
				writeStr((BepStr) object);
				break;
			case INT:
				writeInt((BepInt) object);
				break;
			case LIST:
				writeList((BepList) object);
				break;
			case DICT:
				writeDict((BepDict) object);
				break;
			default:
				throw new IllegalStateException("Unknown type :" + object.getType());
		}
	}

	protected void writeInt(BepInt object) throws IOException {
		outputStream.write(BepObject.FLAG_INT);
		outputStream.write(object.getValue().toString().getBytes(Constant.RAW_CHARSET));
		outputStream.write(BepObject.FLAG_END);
	}

	protected void writeStr(BepStr object) throws IOException {
		byte[] len = Integer.toString(object.getValue().length).getBytes(Constant.RAW_CHARSET);
		outputStream.write(len);
		outputStream.write(BepObject.FLAG_COLON);
		outputStream.write(object.getValue());
	}

	protected void writeList(BepList object) throws IOException {
		outputStream.write(BepObject.FLAG_LIST);
		for (BepObject o : object.getContent()) {
			writeAny(o);
		}
		outputStream.write(BepObject.FLAG_END);
	}

	protected void writeDict(BepDict object) throws IOException {
		outputStream.write(BepObject.FLAG_DICT);
		for (Map.Entry<String, BepObject> entry : object.getContent().entrySet()) {
			writeStr(BepStr.of(entry.getKey(), charset));
			writeAny(entry.getValue());
		}
		outputStream.write(BepObject.FLAG_END);
	}

}
