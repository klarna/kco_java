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
 * File containing the Order unittests when interacting with the connector.
 */
package com.klarna.checkout;

import com.klarna.checkout.stubs.ConnectorStub;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the Order class, interactions with connector.
 */
public class OrderWithConnectorTest {

    /**
     * Connector Stub.
     */
    private ConnectorStub connector;
    /**
     * Order object.
     */
    private Order order;

    /**
     * Set up the whole class. Reset static state.
     */
    @BeforeClass
    public static void setUpClass() {
        Order.setBaseUri(null);
        Order.setContentType("");
    }

    /**
     * Set up the tests.
     */
    @Before
    public void setUp() {
        connector = new ConnectorStub();
        order = new Order(connector);
    }

    /**
     * Reset static state.
     */
    @After
    public void tearDown() {
        Order.setBaseUri(null);
        Order.setContentType("");
    }

    /**
     * Test create.
     *
     * @throws Exception but not really
     */
    @Test
    public void testCreate() throws Exception {
        URI location = new URI("http://stub");

        this.connector.setLocation(location);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "boo");
        order.create(data);

        assertEquals("POST", connector.getApplied("method"));
        assertEquals(order, connector.getApplied("resource"));
        assertNull(
                ((ConnectorOptions) connector.getApplied("options")).getURI());
        assertEquals(
                "Data sent",
                data,
                ((ConnectorOptions) connector.getApplied("options")).getData());
    }

    /**
     * Test fetch.
     *
     * @throws Exception if something really weird is going on.
     */
    @Test
    public void testFetch() throws Exception {
        order.setLocation(new URI("http://klarna.com/foo/bar/15"));
        URI location = order.getLocation();

        order.fetch();

        assertEquals("GET", connector.getApplied("method"));
        assertEquals(order, connector.getApplied("resource"));
        assertEquals(
                location,
                ((ConnectorOptions) connector.getApplied("options")).getURI());
    }

    /**
     * Test the Update function.
     *
     * @throws Exception but not really
     */
    @Test
    public void testUpdate() throws Exception {
        order.setLocation(new URI("http://klarna.com/foo/bar/17"));
        URI location = order.getLocation();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "boo");

        order.update(data);

        assertEquals("POST", connector.getApplied("method"));
        assertEquals(order, connector.getApplied("resource"));
        assertEquals(
                "URL Sent",
                location,
                ((ConnectorOptions) connector.getApplied("options")).getURI());

        assertEquals(
                "Data Sent",
                data,
                ((ConnectorOptions) connector.getApplied("options")).getData());
    }

    /**
     * Test to verify the entry point (Base URI) can be changed.
     *
     * @throws Exception but it won't.
     */
    @Test
    public void testCreateAlternateEntryPoint() throws Exception {
        URI base = new URI("https://checkout.klarna.com/beta/checkout/orders");
        Order.setBaseUri(base);

        Order o = new Order(connector);
        o.create(new HashMap<String, Object>());
        assertEquals(
                "New Base",
                base,
                ((ConnectorOptions) connector.getApplied("options")).getURI());
    }
}
