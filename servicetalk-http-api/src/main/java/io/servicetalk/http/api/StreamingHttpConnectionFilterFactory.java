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

/**
 * A factory for {@link StreamingHttpConnectionFilter}.
 */
@FunctionalInterface
public interface StreamingHttpConnectionFilterFactory extends HttpFilterFactory {

    /**
     * Create a {@link StreamingHttpConnectionFilter} using the provided {@link FilterableStreamingHttpConnection}.
     *
     * @param connection {@link FilterableStreamingHttpConnection} to filter
     * @return {@link StreamingHttpConnectionFilter} using the provided {@link FilterableStreamingHttpConnection}.
     */
    StreamingHttpConnectionFilter create(FilterableStreamingHttpConnection connection);
}
