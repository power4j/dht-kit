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
import com.power4j.kit.bittorrent.core.BepStr;
import com.power4j.kit.bittorrent.common.Constant;
import com.power4j.kit.bittorrent.core.io.StreamWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
class StreamWriterTest {

	@Test
	void writeInt() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamWriter.of(output).writeInt(BepInt.of(23));
		Assertions.assertEquals("i23e", output.toString(Constant.RAW_CHARSET));

		output = new ByteArrayOutputStream();
		StreamWriter.of(output).writeInt(BepInt.of(-23));
		Assertions.assertEquals("i-23e", output.toString(Constant.RAW_CHARSET));

		output = new ByteArrayOutputStream();
		StreamWriter.of(output).writeInt(BepInt.of(0));
		Assertions.assertEquals("i0e", output.toString(Constant.RAW_CHARSET));
	}

	@Test
	void writeStr() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamWriter.of(output).writeStr(BepStr.of("XX", StandardCharsets.UTF_8));
		Assertions.assertEquals("2:XX", output.toString(StandardCharsets.UTF_8));

		output = new ByteArrayOutputStream();
		StreamWriter.of(output).writeStr(BepStr.of("", Constant.RAW_CHARSET));
		Assertions.assertEquals("0:", output.toString(Constant.RAW_CHARSET));
	}

	@Test
	void writeList() throws IOException {
		BepList bepList = BepList.builder().add(1).add("aa").build();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamWriter.of(output).writeList(bepList);
		Assertions.assertEquals("li1e2:aae", output.toString(StandardCharsets.UTF_8));
	}

	@Test
	void writeDict() throws IOException {
		// @formatter:off
		BepDict dict = BepDict.builder()
				.entry("x","y")
				.entry("a",123)
				.entry("l",BepList.of(BepInt.of(456)))
				.build();
		// @formatter:on
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamWriter.of(output).writeDict(dict);
		Assertions.assertEquals("d1:ai123e1:lli456ee1:x1:ye", output.toString(StandardCharsets.UTF_8));
	}

}