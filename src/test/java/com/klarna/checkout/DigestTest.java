/*
 * Copyright 2015 Klarna AB
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

package com.klarna.checkout;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Unit Tests for Digest.
 */
public class DigestTest {

    /**
     * Json string.
     */
    private static final String JSON = "{\"eid\":1245,"
            + "\"goods_list\":[{\"artno\":\"id_1\",\"name\":\"product\","
            + "\"price\":12345,\"vat\":25,\"qty\":1}],"
            + "\"currency\":\"SEK\","
            + "\"country\":\"SWE\","
            + "\"language\":\"SV\"}";

    /**
     * Expected digest.
     */
    private static final String EXPECTED =
            "MO/6KvzsY2y+F+/SexH7Hyg16gFpsPDx5A2PtLZd0Zs=";

    /**
     * Test to create a Digest from a JSON string.
     *
     * @throws Exception if SHA-256 or UTF-8 isn't supported.
     */
    @Test
    public void testDigestString() throws Exception {

        assertEquals(
                "Expected digest hash",
                DigestTest.EXPECTED,
                (new Digest("mySecret")).create(DigestTest.JSON));
    }

    /**
     * Test to create a Digest from a JSON string.
     *
     * @throws Exception but should not happen
     */
    @Test
    public void testDigestStream() throws Exception {

        InputStream stream = new ByteArrayInputStream(
                DigestTest.JSON.getBytes());

        assertEquals(
                "Expected digest hash",
                DigestTest.EXPECTED,
                (new Digest("mySecret")).create(stream));
    }

    /**
     * Test for a null string.
     *
     * @throws Exception if SHA-256 or UTF-8 isn't supported.
     */
    @Test
    public void testDigestNullString() throws Exception {
        assertEquals(
                "Expected digest",
                "0L5zNClDL38A1CXhqwA0Eq+nXUH+KA2Lsus+gv78VrY=",
                (new Digest("mySecret")).create((String) null));
    }

    /**
     * Test for a null input stream.
     *
     * @throws Exception if SHA-256 or UTF-8 isn't supported.
     */
    @Test
    public void testDigestNullStream() throws Exception {
        assertEquals(
                "Expected digest",
                "0L5zNClDL38A1CXhqwA0Eq+nXUH+KA2Lsus+gv78VrY=",
                (new Digest("mySecret")).create((InputStream) null));
    }
}
