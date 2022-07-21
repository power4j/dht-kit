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

package com.power4j.kit.bittorrent.dht.message;

import com.power4j.kit.bittorrent.core.BepInt;
import com.power4j.kit.bittorrent.core.BepList;
import com.power4j.kit.bittorrent.core.BepStr;
import org.springframework.lang.Nullable;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/18
 * @since 1.0
 */
public class ErrorMessage extends AbstractMessage {

	public final static int EC_GENERAL = 201;

	public final static int EC_SERVER = 202;

	public final static int EC_PROTOCOL = 203;

	public final static int EC_UNKNOWN_METHOD = 204;

	public static ErrorMessage generalError(BepStr transaction, @Nullable BepStr version) {
		return new ErrorMessage(transaction, version, BepInt.of(EC_GENERAL), BepStr.of("Generic Error"));
	}

	public static ErrorMessage serverError(BepStr transaction, @Nullable BepStr version) {
		return new ErrorMessage(transaction, version, BepInt.of(EC_SERVER), BepStr.of("Server Error"));
	}

	public static ErrorMessage protocolError(BepStr transaction, @Nullable BepStr version) {
		return new ErrorMessage(transaction, version, BepInt.of(EC_PROTOCOL), BepStr.of("Protocol Error"));
	}

	public static ErrorMessage unknownMethodError(BepStr transaction, @Nullable BepStr version) {
		return new ErrorMessage(transaction, version, BepInt.of(EC_UNKNOWN_METHOD), BepStr.of("Method Unknown"));
	}

	private final BepInt code;

	private final BepStr message;

	public ErrorMessage(BepStr transaction, @Nullable BepStr version, BepInt code, BepStr message) {
		super(transaction, version);
		this.code = code;
		this.message = message;
	}

	@Override
	public MessageType getType() {
		return MessageType.ERROR;
	}

	public BepList getBody() {
		return BepList.of(code, message);
	}

	public BepInt getCode() {
		return code;
	}

	public BepStr getMessage() {
		return message;
	}

}
