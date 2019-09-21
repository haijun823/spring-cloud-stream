/*
 * Copyright 2019-2019 the original author or authors.
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

package org.springframework.cloud.stream.function;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cloud.stream.binding.BindableProxyFactory;
import org.springframework.cloud.stream.binding.BoundTargetHolder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * {@link FactoryBean} for creating inputs/outputs destinations to be bound to
 * function arguments. It is an extension to {@link BindableProxyFactory} which
 * operates on Bindable interfaces (e.g., Source, Processor, Sink) which internally
 * define inputs and output channels. Unlike BindableProxyFactory, this class
 * operates based on the count of provided inputs and outputs deriving the binding
 * (channel) names based on convention - {@code `<function-definition>. + <in/out> + .<index>`}
 * <br>
 * For example, `myFunction.in.0` - is the binding for the first input argument of the
 * function with the name `myFunction`.
 *
 * @author Oleg Zhurakousky
 *
 * @since 3.0
 */
class BindableFunctionProxyFactory extends BindableProxyFactory {

	static final String delimiter = ".";

	private final int inputCount;

	private final int outputCount;

	private final String functionDefinition;

	private final boolean nameBasedOnFunctionName;

	private boolean multiple;

	BindableFunctionProxyFactory(String functionDefinition, int inputCount, int outputCount, boolean nameBasedOnFunctionName) {
		super(null);
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.functionDefinition = functionDefinition;
		this.nameBasedOnFunctionName = nameBasedOnFunctionName;
	}


	@Override
	public void afterPropertiesSet() {
		Assert.notEmpty(BindableFunctionProxyFactory.this.bindingTargetFactories,
				"'bindingTargetFactories' cannot be empty");

		this.multiple = this.inputCount > 1 || this.outputCount > 1;

		if (this.inputCount > 0) {
			if (multiple || nameBasedOnFunctionName) {
				for (int i = 0; i < inputCount; i++) {
					this.createInput(this.buildInputNameForIndex(i));
				}
			}
			else {
				this.createInput("input");
			}
		}

		if (this.outputCount > 0) {
			if (multiple || nameBasedOnFunctionName) {
				for (int i = 0; i < outputCount; i++) {
					this.createOutput(this.buildOutputNameForIndex(i));
				}
			}
			else {
				this.createOutput("output");
			}
		}
	}

	@Override
	public Class<?> getObjectType() {
		return this.type;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	protected boolean isNameBasedOnFunctionName() {
		return this.nameBasedOnFunctionName;
	}

	protected String getFunctionDefinition() {
		return this.functionDefinition;
	}

	protected String getInputName(int index) {
		return CollectionUtils.isEmpty(this.getInputs())
				? null
						: this.getInputs().toArray(new String[0])[index];
	}

	protected String getOutputName(int index) {
		return CollectionUtils.isEmpty(this.getOutputs())
				? null
						: this.getOutputs().toArray(new String[0])[index];
	}

	protected boolean isMultiple() {
		return this.multiple;
	}

	private String buildInputNameForIndex(int index) {
		return new StringBuilder(this.functionDefinition)
			.append(delimiter)
			.append("in")
			.append(delimiter)
			.append(index)
			.toString();
	}

	private String buildOutputNameForIndex(int index) {
		return new StringBuilder(this.functionDefinition)
				.append(delimiter)
				.append("out")
				.append(delimiter)
				.append(index)
				.toString();
	}

	private void createInput(String name) {
		BindableFunctionProxyFactory.this.inputHolders.put(name,
				new BoundTargetHolder(getBindingTargetFactory(SubscribableChannel.class)
						.createInput(name), true));
	}

	private void createOutput(String name) {
		BindableFunctionProxyFactory.this.outputHolders.put(name,
				new BoundTargetHolder(getBindingTargetFactory(MessageChannel.class)
						.createOutput(name), true));
	}

}
