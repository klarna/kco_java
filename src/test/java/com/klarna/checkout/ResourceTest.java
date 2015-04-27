/*
 * Copyright 2013 Klarna AB
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the resource classes.
 */
@RunWith(Parameterized.class)
public class ResourceTest {
    /**
     * Resource under test.
     */
    private Resource resource;

    /**
     * Get a list of resources to test.
     *
     * @return Resource The resource to test
     *
     * @throws URISyntaxException But not really
     */
    @Parameterized.Parameters
    public static Collection<Resource[]> data() throws URISyntaxException {
        IConnector conn = mock(IConnector.class);
        when(conn.getBaseUri()).thenReturn("http://zoidberg.com/");

        return Arrays.asList(new Resource[][] {
                {new Order(conn)},
                {new RecurringStatus(conn, "ABC")},
                {new RecurringOrder(conn, "ABC")}
        });
    }

    /**
     * Constructor.
     *
     * @param current Resource to test
     */
    public ResourceTest(final Resource current) {
        this.resource = current;
    }

    /**
     * Test correct contentType is used.
     */
    @Test
    public void testContentType() {
        this.resource.setContentType("klarna/json");

        assertEquals(
                "klarna/json",
                this.resource.getContentType());
    }

    /**
     * Ensure that accept header can be set.
     */
    @Test
    public void testAccept() {
        this.resource.setAccept("klarna/whatever+json");

        assertEquals(
                "klarna/whatever+json",
                this.resource.getAccept());
    }

    /**
     * Test that location can be set.
     *
     * @throws URISyntaxException but no.
     */
    @Test
    public void testLocation() throws URISyntaxException {
        URI uri = new URI("http://www.klarna.com");

        this.resource.setLocation(uri);

        assertEquals(uri, this.resource.getLocation());
    }

    /**
     * Test that output of marshal works as input for parse.
     */
    @Test
    public void testParseMarshalIdentity() {
        HashMap<String, Object> data = new HashMap<String, Object>() {
            { put("foo", "boo"); }
        };

        this.resource.parse(data);

        assertEquals(data, this.resource.marshal());
    }

    /**
     * Test so you can get a key that has been parsed.
     */
    @Test
    public void testGet() {
        HashMap<String, Object> data = new HashMap<String, Object>() {
            { put("foo", "boo"); }
        };

        this.resource.parse(data);

        assertEquals("boo", this.resource.get("foo"));
    }

    /**
     * Test that marshal gets the correct keys.
     */
    @Test
    public void testMarshalHasCorrectKeys() {
        HashMap<String, Object> data = new HashMap<String, Object>() {
            { put("testKey1", "testValue1"); }
            { put("testKey2", "testValue2"); }
        };

        this.resource.parse(data);
        Map<String, String> marshal = this.resource.marshal();

        assertTrue(marshal.containsKey("testKey1"));
        assertTrue(marshal.containsKey("testKey2"));

        assertEquals("testValue1", marshal.get("testKey1"));
        assertEquals("testValue2", marshal.get("testKey2"));
    }
}
