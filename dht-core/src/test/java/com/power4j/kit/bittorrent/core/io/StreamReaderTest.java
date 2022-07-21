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
import com.power4j.kit.bittorrent.core.io.StreamReader;
import com.power4j.kit.bittorrent.exceptions.InvalidChunkException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/15
 * @since 1.0
 */
class StreamReaderTest {

	@Test
	void throwIfEof() {
		Assertions.assertThrows(EOFException.class, () -> StreamReader.of("").readAny());
		Assertions.assertThrows(EOFException.class, () -> StreamReader.of(new byte[0]).readAny());
	}

	@Test
	void readStr() throws IOException {

		BepStr bepStr = StreamReader.of("2:xx").readStr();
		Assertions.assertEquals("xx", bepStr.asString());

		bepStr = StreamReader.of("0:").readStr();
		Assertions.assertEquals("", bepStr.asString());

		Assertions.assertThrows(InvalidChunkException.class, () -> StreamReader.of("x").readStr());
		Assertions.assertThrows(EOFException.class, () -> StreamReader.of("2:x").readStr());
	}

	@Test
	void readInt() throws IOException {
		BepInt bepInt = StreamReader.of("i23e").readInt();
		Assertions.assertEquals(23, bepInt.getValue().intValue());

		bepInt = StreamReader.of("i-23e").readInt();
		Assertions.assertEquals(-23, bepInt.getValue().intValue());

		bepInt = StreamReader.of("i1234567890123456789e").readInt();
		Assertions.assertEquals(1234567890123456789L, bepInt.getValue().longValue());

		bepInt = StreamReader.of("i0e").readInt();
		Assertions.assertEquals(0, bepInt.getValue().intValue());

		ByteArrayInputStream in = new ByteArrayInputStream("i0eeeeee".getBytes());
		bepInt = new StreamReader(in).readInt();
		Assertions.assertEquals(0, bepInt.getValue().intValue());
		Assertions.assertThrows(InvalidChunkException.class, () -> new StreamReader(in).readAny());
	}

	@Test
	void readList() throws IOException {
		BepList bepList = StreamReader.of("l3:xxxi2ee").readList();
		Assertions.assertEquals(2, bepList.getContent().size());
		Assertions.assertEquals(BepStr.class, bepList.getContent().get(0).getClass());
		Assertions.assertEquals("xxx", (((BepStr) bepList.getContent().get(0))).asString());
		Assertions.assertEquals(BepInt.class, bepList.getContent().get(1).getClass());
		Assertions.assertEquals(2, (((BepInt) bepList.getContent().get(1))).getValue().intValue());
	}

	@Test
	void readDict() throws IOException {
		BepDict bepDict = StreamReader.of("d3:xxxi2ee").readDict();
		Assertions.assertEquals(1, bepDict.getContent().size());
		Assertions.assertEquals(BepInt.class, bepDict.getContent().get("xxx").getClass());
		Assertions.assertEquals(2, ((BepInt) bepDict.getContent().get("xxx")).getValue().intValue());

		bepDict = StreamReader.of("d3:xxxl3:xxxi2eee").readDict();
		Assertions.assertEquals(1, bepDict.getContent().size());
		Assertions.assertEquals(BepList.class, bepDict.getContent().get("xxx").getClass());
		Assertions.assertEquals(2, ((BepList) bepDict.getContent().get("xxx")).getContent().size());
	}

}