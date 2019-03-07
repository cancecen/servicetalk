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
package io.servicetalk.concurrent.api;

import io.servicetalk.concurrent.internal.ServiceTalkTestTimeout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConcatWithOrderingTest {

    @Rule
    public final Timeout timeout = new ServiceTalkTestTimeout();

    protected final StringBuilder sb = new StringBuilder();

    @Test
    public void completablesOnly() throws Exception {
        completable(1)
                .concatWith(completable(2))
                .concatWith(completable(3))
                .concatWith(completable(4))
                .concatWith(completable(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void completableSingeCompletables() throws Exception {
        completable(1)
                .concatWith(single(2))
                .concatWith(completable(3))
                .concatWith(completable(4))
                .concatWith(completable(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void completableTwoSingesCompletables() throws Exception {
        completable(1)
                .concatWith(single(2))
                .concatWith(single(3))
                .concatWith(completable(4))
                .concatWith(completable(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void completablePublisherCompletables() throws Exception {
        completable(1)
                .concatWith(publisher(2))
                .concatWith(completable(3))
                .concatWith(completable(4))
                .concatWith(completable(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void singlesOnly() throws Exception {
        single(1)
                .concatWith(single(2))
                .concatWith(single(3))
                .concatWith(single(4))
                .concatWith(single(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void singleCompletableSingles() throws Exception {
        single(1)
                .concatWith(completable(2))
                .concatWith(single(3))
                .concatWith(single(4))
                .concatWith(single(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void singleTwoCompletablesSingles() throws Exception {
        single(1)
                .concatWith(completable(2))
                .concatWith(completable(3))
                .concatWith(single(4))
                .concatWith(single(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void singlePublisherSingles() throws Exception {
        single(1)
                .concatWith(publisher(2))
                .concatWith(single(3))
                .concatWith(single(4))
                .concatWith(single(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void publishersOnly() throws Exception {
        publisher(1)
                .concatWith(publisher(2))
                .concatWith(publisher(3))
                .concatWith(publisher(4))
                .concatWith(publisher(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void publisherCompletablePublishers() throws Exception {
        publisher(1)
                .concatWith(completable(2))
                .concatWith(publisher(3))
                .concatWith(publisher(4))
                .concatWith(publisher(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void publisherSinglePublishers() throws Exception {
        publisher(1)
                .concatWith(single(2))
                .concatWith(publisher(3))
                .concatWith(publisher(4))
                .concatWith(publisher(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void completableSinglePublisherSingleCompletable() throws Exception {
        completable(1)
                .concatWith(single(2))
                .concatWith(publisher(3))
                .concatWith(single(4))
                .concatWith(completable(5))
                .toFuture().get();

        assertResult(sb);
    }

    @Test
    public void publisherSingleCompletableSinglePublisher() throws Exception {
        publisher(1)
                .concatWith(single(2))
                .concatWith(completable(3))
                .concatWith(single(4))
                .concatWith(publisher(5))
                .toFuture().get();

        assertResult(sb);
    }

    private static void assertResult(final StringBuilder sb) {
        assertThat(sb.toString(), is("12345"));
    }

    private Completable completable(final int number) {
        return Completable.completed().doBeforeComplete(() -> sb.append(number));
    }

    private Single<Integer> single(final int number) {
        return Single.success(0).doBeforeSuccess(__ -> sb.append(number));
    }

    private Publisher<Integer> publisher(final int number) {
        return Publisher.from(0, 1, 2).doBeforeComplete(() -> sb.append(number));
    }
}