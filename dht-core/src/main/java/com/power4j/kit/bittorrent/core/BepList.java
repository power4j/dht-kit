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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/6/20
 * @since 1.0
 */
public class BepList implements BepObject {

	private final List<BepObject> list;

	public static Builder builder() {
		return new Builder();
	}

	public static BepList of(BepObject... objects) {
		return new BepList(Arrays.asList(objects));
	}

	public BepList(List<? extends BepObject> list) {
		this.list = new ArrayList<>(list);
	}

	public BepList() {
		this(new ArrayList<>(4));
	}

	@Override
	public ChunkType getType() {
		return ChunkType.LIST;
	}

	public void add(BepObject value) {
		list.add(value);
	}

	public List<BepObject> getContent() {
		return list;
	}

	public static class Builder {

		private final List<BepObject> list = new ArrayList<>(8);

		public Builder add(long value) {
			return add(BepInt.of(value));
		}

		public Builder add(String value) {
			return add(value, StandardCharsets.UTF_8);
		}

		public Builder add(String value, Charset charset) {
			return add(BepStr.of(value, charset));
		}

		public Builder add(BepObject value) {
			list.add(value);
			return this;
		}

		public BepList build() {
			return new BepList(list);
		}

	}

}
