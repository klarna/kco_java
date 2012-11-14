/*
 * Copyright 2012 Klarna AB
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
 *
 * File containing the Order unittests
 */
package com.klarna.checkout;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Tests for the Order class, basic functionality.
 */
public class OrderTest {

    /**
     * Object under test.
     */
    private Order order;

    /**
     * Set up tests.
     */
    @Before
    public void setUp() {
        IConnector conn = mock(IConnector.class);
        order = new Order(conn);
    }

    /**
     * Test correct contentType is used.
     */
    @Test
    public void testContentType() {
        Order.setContentType("klarna/json");
        assertEquals(
                "klarna/json",
                order.getContentType());
    }

    /**
     * Test that location is initialized as null.
     */
    @Test
    public void testGetLocationEmpty() {
        assertNull(order.getLocation());
    }

    /**
     * Test that location can be set.
     *
     * @throws URISyntaxException but no.
     */
    @Test
    public void testSetLocation() throws URISyntaxException {
        URI uri = new URI("http://www.klarna.com");
        order.setLocation(uri);
        assertEquals(uri, order.getLocation());
    }

    /**
     * Test that output of marshal works as input for parse.
     */
    @Test
    public void testParseMarshalIdentity() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "boo");
        order.parse(data);
        assertEquals(data, order.marshal());
    }

    /**
     * Test so you can get a key that has been parsed.
     */
    @Test
    public void testGet() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "boo");
        order.parse(data);
        assertEquals("boo", order.get("foo"));
    }

    /**
     * Test that marshal gets the correct keys.
     */
    @Test
    public void testMarshalHasCorrectKeys() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("testKey1", "testValue1");
        data.put("testKey2", "testValue2");
        order.parse(data);
        Map<String, String> marshal = order.marshal();
        assertTrue(marshal.containsKey("testKey1"));
        assertTrue(marshal.containsKey("testKey2"));
        assertEquals("testValue1", marshal.get("testKey1"));
        assertEquals("testValue2", marshal.get("testKey2"));
    }
}
