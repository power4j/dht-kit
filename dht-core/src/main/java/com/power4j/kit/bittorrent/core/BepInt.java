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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigInteger;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/6/20
 * @since 1.0
 */
public class BepInt extends ValueObject<BigInteger> {

	private final BigInteger value;

	public static BepInt of(long value) {
		return new BepInt(BigInteger.valueOf(value));
	}

	public BepInt(BigInteger value) {
		this.value = value;
	}

	@Override
	public ChunkType getType() {
		return ChunkType.INT;
	}

	@Override
	public BigInteger getValue() {
		return value;
	}

	public long asLong() {
		return value.longValue();
	}

	public long asInt() {
		return value.intValue();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		BepInt btInt = (BepInt) o;

		return new EqualsBuilder().append(value, btInt.value).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(value).toHashCode();
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
