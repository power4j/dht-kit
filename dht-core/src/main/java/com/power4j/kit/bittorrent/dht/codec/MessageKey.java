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

package com.power4j.kit.bittorrent.dht.codec;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public interface MessageKey {

	String KEY_TRANSACTION_ID = "t";

	String KEY_TYPE = "y";

	String KEY_VERSION = "v";

	String KEY_QUERY_METHOD = "q";

	String KEY_QUERY_ARGV = "a";

	String KEY_RESPONSE_BODY = "r";

	String KEY_ERROR_BODY = "e";

	String KEY_ID = "id";

	String KEY_TARGET = "target";

	String KEY_NODES = "nodes";

	String KEY_INFO_HASH = "info_hash";

	String KEY_TOKEN = "token";

	String KEY_VALUES = "values";

	String KEY_PORT = "port";

	String KEY_IMPLIED_PORT = "implied_port";

}
