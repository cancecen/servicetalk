/*
 * Copyright © 2019, 2021 Apple Inc. and the ServiceTalk project authors
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

import io.servicetalk.http.api.DefaultHttpExecutionContext;
import io.servicetalk.http.api.HttpExecutionContext;
import io.servicetalk.http.api.HttpExecutionStrategy;
import io.servicetalk.transport.netty.internal.ExecutionContextBuilder;

import static io.servicetalk.http.api.HttpExecutionStrategies.defaultStrategy;

final class HttpExecutionContextBuilder extends ExecutionContextBuilder<HttpExecutionStrategy> {

    HttpExecutionContextBuilder() {
    }

    HttpExecutionContextBuilder(final HttpExecutionContextBuilder from) {
        super(from);
    }

    /**
     * Builds a new {@link HttpExecutionContext}.
     *
     * @return {@link HttpExecutionContext}.
     */
    @Override
    public HttpExecutionContext build() {
        return new DefaultHttpExecutionContext(
                allocator == null ? defaultContextSupplier.get().bufferAllocator() : allocator,
                ioExecutor == null ? defaultContextSupplier.get().ioExecutor() : ioExecutor,
                executor == null ? defaultContextSupplier.get().executor() : executor,
                strategy == null ? defaultStrategy() : strategy);
    }
}
