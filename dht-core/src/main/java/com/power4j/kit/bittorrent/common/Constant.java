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

package com.power4j.kit.bittorrent.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/20
 * @since 1.0
 */
public interface Constant {

	int EOF = -1;

	int LENGTH_INFO_HASH = 20;

	Charset RAW_CHARSET = StandardCharsets.ISO_8859_1;

	Charset DEFAULT_TEXT_CHARSET = StandardCharsets.UTF_8;

	char CHAR_ZERO = '0';

	char CHAR_NINE = '9';

}
