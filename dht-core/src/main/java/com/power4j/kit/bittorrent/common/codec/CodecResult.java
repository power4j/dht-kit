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

package com.power4j.kit.bittorrent.common.codec;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/21
 * @since 1.0
 */
@Getter
public class CodecResult<T> {

	private final CodecStatus status;

	private final String message;

	@Nullable
	private final T data;

	public static <T> CodecResult<T> of(CodecStatus status, @Nullable T data) {
		return of(status, "", data);
	}

	public static <T> CodecResult<T> of(CodecStatus status, String message) {
		return of(status, message, null);
	}

	public static <T> CodecResult<T> of(CodecStatus status, String message, @Nullable T data) {
		return new CodecResult<>(status, message, data);
	}

	CodecResult(CodecStatus status, String message, @Nullable T data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public boolean isDone() {
		return status == CodecStatus.DONE || status == CodecStatus.DONE_WARN;
	}

	public boolean isError() {
		return status == CodecStatus.ERROR;
	}

	public boolean isCompleted() {
		return status != CodecStatus.PENDING;
	}

	public Optional<T> useData() {
		return Optional.ofNullable(data);
	}

	public T requireData() {
		return useData().orElseThrow(NoSuchElementException::new);
	}

}
