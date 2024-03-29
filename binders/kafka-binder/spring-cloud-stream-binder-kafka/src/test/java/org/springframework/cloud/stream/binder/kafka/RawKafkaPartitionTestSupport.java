/*
 * Copyright 2014-2023 the original author or authors.
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

package org.springframework.cloud.stream.binder.kafka;

import org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy;
import org.springframework.cloud.stream.binder.PartitionSelectorStrategy;
import org.springframework.messaging.Message;

/**
 * @author Marius Bogoevici
 */
class RawKafkaPartitionTestSupport
		implements PartitionKeyExtractorStrategy, PartitionSelectorStrategy {

	@Override
	public int selectPartition(Object key, int divisor) {
		return ((byte[]) key)[0] % divisor;
	}

	@Override
	public Object extractKey(Message<?> message) {
		return message.getPayload();
	}

}
