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

package com.power4j.bittorrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.netty.Connection;
import reactor.netty.resources.LoopResources;

import java.util.concurrent.CompletableFuture;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/19
 * @since 1.0
 */
@Slf4j
@SpringBootApplication
public class DhtDemo implements CommandLineRunner {

	@Override
	public void run(String... args) {
		CompletableFuture.runAsync(this::runClient).join();
	}

	public static void main(String[] args) {
		SpringApplication.run(DhtDemo.class, args);
	}

	private void runClient() {
		LoopResources loop = LoopResources.create("event-loop", 1, 4, true);
		Connection connection = new Client(loop, null, 46005).run("67.215.246.10", 6881);
		log.info("connection running");
		connection.onDispose().block();
		log.info("connection closed");
	}

}
