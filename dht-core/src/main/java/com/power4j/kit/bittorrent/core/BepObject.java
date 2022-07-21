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

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/6/20
 * @since 1.0
 * @see <a href='http://www.bittorrent.org/beps/bep_0003.html'>bep_0003</a>
 */
public interface BepObject {

	int FLAG_COLON = ':';

	int FLAG_END = 'e';

	int FLAG_INT = 'i';

	int FLAG_LIST = 'l';

	int FLAG_DICT = 'd';

	/**
	 * The type
	 * @return type of this object
	 */
	ChunkType getType();

}
