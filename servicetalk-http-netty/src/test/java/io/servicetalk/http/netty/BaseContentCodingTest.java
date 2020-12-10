/*
 * Copyright © 2020 Apple Inc. and the ServiceTalk project authors
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

import io.servicetalk.concurrent.internal.ServiceTalkTestTimeout;
import io.servicetalk.encoding.api.ContentCodec;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runners.Parameterized;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static io.servicetalk.encoding.api.ContentCodings.deflateDefault;
import static io.servicetalk.encoding.api.ContentCodings.gzipDefault;
import static io.servicetalk.encoding.api.ContentCodings.identity;
import static io.servicetalk.http.netty.HttpProtocol.HTTP_1;
import static io.servicetalk.http.netty.HttpProtocol.HTTP_2;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public abstract class BaseContentCodingTest {

    private static final int PAYLOAD_SIZE = 1024;

    @Rule
    public final Timeout timeout = new ServiceTalkTestTimeout();

    protected final Scenario scenario;

    public BaseContentCodingTest(final HttpProtocol protocol, final Codings serverCodings,
                                 final Codings clientCodings, final Compression compression,
                                 final boolean valid) {
        this.scenario = new Scenario(compression.codec, clientCodings.list, serverCodings.list, protocol, valid);
    }

    @Parameterized.Parameters(name = "{index}, protocol={0}, server=[{1}], client=[{2}], request={3}, pass={4}")
    public static Object[][] params() {
        return new Object[][] {
                {HTTP_1, Codings.DEFAULT, Codings.DEFAULT, Compression.ID, true},
                {HTTP_2, Codings.DEFAULT, Codings.DEFAULT, Compression.ID, true},
                {HTTP_1, Codings.DEFAULT, Codings.GZIP_ID, Compression.GZIP, false},
                {HTTP_2, Codings.DEFAULT, Codings.GZIP_ID, Compression.GZIP, false},
                {HTTP_1, Codings.DEFAULT, Codings.DEFLATE_ID, Compression.DEFLATE, false},
                {HTTP_2, Codings.DEFAULT, Codings.DEFLATE_ID, Compression.DEFLATE, false},
                {HTTP_1, Codings.GZIP_DEFLATE_ID, Codings.DEFAULT, Compression.ID, true},
                {HTTP_2, Codings.GZIP_DEFLATE_ID, Codings.DEFAULT, Compression.ID, true},
                {HTTP_1, Codings.ID_GZIP_DEFLATE, Codings.GZIP_ID, Compression.GZIP, true},
                {HTTP_2, Codings.ID_GZIP_DEFLATE, Codings.GZIP_ID, Compression.GZIP, true},
                {HTTP_1, Codings.ID_GZIP_DEFLATE, Codings.DEFLATE_ID, Compression.DEFLATE, true},
                {HTTP_2, Codings.ID_GZIP_DEFLATE, Codings.DEFLATE_ID, Compression.DEFLATE, true},
                {HTTP_1, Codings.ID_GZIP, Codings.DEFLATE_ID, Compression.DEFLATE, false},
                {HTTP_2, Codings.ID_GZIP, Codings.DEFLATE_ID, Compression.DEFLATE, false},
                {HTTP_1, Codings.ID_DEFLATE, Codings.GZIP_ID, Compression.GZIP, false},
                {HTTP_2, Codings.ID_DEFLATE, Codings.GZIP_ID, Compression.GZIP, false},
                {HTTP_1, Codings.ID_DEFLATE, Codings.DEFLATE_ID, Compression.DEFLATE, true},
                {HTTP_2, Codings.ID_DEFLATE, Codings.DEFLATE_ID, Compression.DEFLATE, true},
                {HTTP_1, Codings.ID_DEFLATE, Codings.DEFAULT, Compression.ID, true},
                {HTTP_2, Codings.ID_DEFLATE, Codings.DEFAULT, Compression.ID, true},
                {HTTP_1, Codings.GZIP_ONLY, Codings.ID_ONLY, Compression.ID, true},
                {HTTP_2, Codings.GZIP_ONLY, Codings.ID_ONLY, Compression.ID, true},
                {HTTP_1, Codings.GZIP_ONLY, Codings.GZIP_ID, Compression.ID, true},
                {HTTP_2, Codings.GZIP_ONLY, Codings.GZIP_ID, Compression.ID, true},
                {HTTP_1, Codings.GZIP_ONLY, Codings.GZIP_ID, Compression.ID, true},
                {HTTP_2, Codings.GZIP_ONLY, Codings.GZIP_ID, Compression.ID, true},
                {HTTP_1, Codings.GZIP_ONLY, Codings.GZIP_ID, Compression.GZIP, true},
                {HTTP_2, Codings.GZIP_ONLY, Codings.GZIP_ID, Compression.GZIP, true},
                {HTTP_1, Codings.DEFAULT, Codings.GZIP_ID, Compression.GZIP, false},
                {HTTP_2, Codings.DEFAULT, Codings.GZIP_ID, Compression.GZIP, false},
                {HTTP_1, Codings.DEFAULT, Codings.GZIP_DEFLATE_ID, Compression.DEFLATE, false},
                {HTTP_2, Codings.DEFAULT, Codings.GZIP_DEFLATE_ID, Compression.DEFLATE, false},
                {HTTP_1, Codings.DEFAULT, Codings.GZIP_ID, Compression.ID, true},
                {HTTP_2, Codings.DEFAULT, Codings.GZIP_ID, Compression.ID, true},
        };
    }

    @Test
    public void testCompatibility() throws Throwable {
        if (scenario.valid) {
            assertSuccessful(scenario.requestEncoding);
        } else {
            assertNotSupported(scenario.requestEncoding);
        }
    }

    protected abstract void assertSuccessful(ContentCodec requestEncoding) throws Throwable;

    protected abstract void assertNotSupported(ContentCodec requestEncoding) throws Throwable;

    protected static byte[] payload(byte b) {
        byte[] payload = new byte[PAYLOAD_SIZE];
        Arrays.fill(payload, b);
        return payload;
    }

    protected static String payloadAsString(byte b) {
        return new String(payload(b), StandardCharsets.US_ASCII);
    }

    protected enum Codings {
        DEFAULT(emptyList()),
        GZIP_ONLY(singletonList(gzipDefault())),
        GZIP_ID(asList(gzipDefault(), identity())),
        GZIP_DEFLATE_ID(asList(gzipDefault(), deflateDefault(), identity())),
        ID_ONLY(singletonList(identity())),
        ID_GZIP(asList(identity(), gzipDefault())),
        ID_DEFLATE(asList(identity(), deflateDefault())),
        ID_GZIP_DEFLATE(asList(identity(), gzipDefault(), deflateDefault())),
        DEFLATE_ONLY(singletonList(deflateDefault())),
        DEFLATE_ID(asList(deflateDefault(), identity()));

        List<ContentCodec> list;

        Codings(List<ContentCodec> list) {
            this.list = list;
        }

        public String toString() {
            if (list.isEmpty()) {
                return "identity";
            }

            StringBuilder b = new StringBuilder();
            for (ContentCodec c : list) {
                if (b.length() > 1) {
                    b.append(", ");
                }

                b.append(c.name());
            }
            return b.toString();
        }
    }

    protected enum Compression {
        ID(identity()),
        GZIP(gzipDefault()),
        DEFLATE(deflateDefault());

        ContentCodec codec;

        Compression(ContentCodec codec) {
            this.codec = codec;
        }

        public String toString() {
            return codec.name().toString();
        }
    }

    protected static class Scenario {
        final ContentCodec requestEncoding;
        final List<ContentCodec> clientSupported;
        final List<ContentCodec> serverSupported;
        final HttpProtocol protocol;
        final boolean valid;

        Scenario(final ContentCodec requestEncoding,
                 final List<ContentCodec> clientSupported, final List<ContentCodec> serverSupported,
                 final HttpProtocol protocol, final boolean valid) {
            this.requestEncoding = requestEncoding;
            this.clientSupported = clientSupported;
            this.serverSupported = serverSupported;
            this.protocol = protocol;
            this.valid = valid;
        }
    }
}
