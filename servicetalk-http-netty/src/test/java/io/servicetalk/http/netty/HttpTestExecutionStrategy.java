/*
 * Copyright © 2019 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.http.netty;

import io.servicetalk.http.api.HttpExecutionStrategies;
import io.servicetalk.http.api.HttpExecutionStrategy;

import java.util.function.Supplier;

public enum HttpTestExecutionStrategy {
    NO_OFFLOAD(HttpExecutionStrategies::noOffloadsStrategy),
    DEFAULT(HttpExecutionStrategies::defaultStrategy);

    final Supplier<HttpExecutionStrategy> executorSupplier;

    HttpTestExecutionStrategy(final Supplier<HttpExecutionStrategy> executorSupplier) {
        this.executorSupplier = executorSupplier;
    }
}
