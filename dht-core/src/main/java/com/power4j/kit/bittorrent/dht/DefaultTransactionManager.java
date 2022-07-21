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

package com.power4j.kit.bittorrent.dht;

import com.power4j.kit.bittorrent.core.BepStr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public class DefaultTransactionManager implements TransactionManager {

	private final AtomicInteger value = new AtomicInteger();

	private final int MAX = 0xFFFF;

	@Override
	public BepStr next() {
		int n = value.updateAndGet(i -> (i >= MAX) ? 0 : i + 1);
		return BepStr.of(ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort((short) n).array());
	}

}
