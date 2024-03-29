/*
 * Copyright 2022-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.stream.kotlin

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.function.context.PollableBean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux

@Configuration
@EnableAutoConfiguration
open class KotlinTestConfiguration {

	@PollableBean // it doesn't work with Kotlin lambda
	open fun produceNames(): () -> Flux<String> = {
		Flux.just(
			"Ricky",
			"Julien",
			"Bubbles"
		)
	}

}
