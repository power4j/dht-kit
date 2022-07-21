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

import org.springframework.lang.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/6/20
 * @since 1.0
 */
public class BepDict implements BepObject {

	private final SortedMap<String, BepObject> map;

	public static Builder builder() {
		return new Builder();
	}

	public static BepDict of(String key, BepObject value) {
		return builder().entry(key, value).build();
	}

	public BepDict(Map<String, BepObject> map) {
		this.map = new TreeMap<>(map);
	}

	public BepDict() {
		this(new LinkedHashMap<>());
	}

	@Override
	public ChunkType getType() {
		return ChunkType.DICT;
	}

	public SortedMap<String, BepObject> getContent() {
		return map;
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	@Nullable
	public BepObject get(String key) {
		return map.get(key);
	}

	public Optional<BepObject> find(String key) {
		return Optional.ofNullable(map.get(key));
	}

	@SuppressWarnings("unchecked")
	public <T extends BepObject> Optional<T> find(String key, Class<T> cls) {
		return Optional.ofNullable(map.get(key)).filter(o -> cls.isAssignableFrom(o.getClass())).map(o -> (T) o);
	}

	@Nullable
	public BepObject put(String key, BepObject value) {
		return map.put(key, value);
	}

	public static class Builder {

		private final Map<String, BepObject> map = new HashMap<>();

		public Builder entry(String key, BepObject value) {
			map.put(key, value);
			return this;
		}

		public Builder entry(String key, String value) {
			return entry(key, value, StandardCharsets.UTF_8);
		}

		public Builder entry(String key, String value, Charset charset) {
			return entry(key, BepStr.of(value, charset));
		}

		public Builder entry(String key, long value) {
			return entry(key, BepInt.of(value));
		}

		public BepDict build() {
			return new BepDict(map);
		}

	}

}
