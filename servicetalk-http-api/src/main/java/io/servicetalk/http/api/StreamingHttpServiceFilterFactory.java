/*
 * Copyright © 2018, 2021 Apple Inc. and the ServiceTalk project authors
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
package io.servicetalk.http.api;

import static io.servicetalk.http.api.DefaultHttpExecutionStrategy.OFFLOAD_RECEIVE_META_STRATEGY;

/**
 * A factory for {@link StreamingHttpServiceFilter}.
 */
@FunctionalInterface
public interface StreamingHttpServiceFilterFactory extends HttpFilterFactory {

    /**
     * Create a {@link StreamingHttpServiceFilter} using the provided {@link StreamingHttpService}.
     *
     * @param service {@link StreamingHttpService} to filter
     * @return {@link StreamingHttpServiceFilter} using the provided {@link StreamingHttpService}.
     */
    StreamingHttpServiceFilter create(StreamingHttpService service);

    @Override
    default HttpExecutionStrategy requiredOffloads() {
        return OFFLOAD_RECEIVE_META_STRATEGY;
    }
}
