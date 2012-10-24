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

import com.klarna.checkout.stubs.ConnectorStub;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
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
     * Set up the tests.
     */
    @Before
    public void setUp() {
        connector = new ConnectorStub();
        order = new Order();
    }

    /**
     * Test create.
     *
     * @throws URISyntaxException but not really
     */
    @Test
    public void testCreate() throws URISyntaxException {
        URI location = new URI("http://stub");
        this.connector.setLocation(location);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "boo");
        order.parse(data);
        order.create(this.connector);

        assertEquals("boo", order.get("foo"));
        assertEquals("boo", order.marshal().get("foo"));
        assertEquals("POST", connector.getApplied("method"));
        assertEquals(order, connector.getApplied("resource"));
        assertNull(
            ((ConnectorOptions) this.connector.getApplied("options")).getURI());
    }

    /**
     * Test fetch.
     *
     * @throws URISyntaxException if something really weird is going on.
     */
    @Test
    public void testFetch() throws URISyntaxException {
        order.setLocation(new URI("http://klarna.com/foo/bar/15"));
        URI location = order.getLocation();

        order.fetch(this.connector);

        assertEquals("GET", connector.getApplied("method"));
        assertEquals(order, connector.getApplied("resource"));
        assertEquals(
            location,
            ((ConnectorOptions) this.connector.getApplied("options")).getURI());
    }

    /**
     * Test so fetch sets the location when an URI is supplied.
     *
     * @throws URISyntaxException but it doesn't really.
     */
    @Test
    public void testFetchSetLocation() throws URISyntaxException {
        URI uri = new URI("http://klarna.com/foo/bar/16");

        order.fetch(this.connector, uri);
        assertEquals("GET", connector.getApplied("method"));
        assertEquals(order, connector.getApplied("resource"));
        assertEquals(
            "URL Sent",
            uri,
            ((ConnectorOptions) this.connector.getApplied("options")).getURI());
        assertEquals(
            "resource location",
            uri,
            order.getLocation());
    }

    /**
     * Test the Update function.
     *
     * @throws URISyntaxException it's lying!
     */
    @Test
    public void testUpdate() throws URISyntaxException {
        order.setLocation(new URI("http://klarna.com/foo/bar/17"));
        URI location = order.getLocation();

        order.update(this.connector);
        assertEquals("POST", connector.getApplied("method"));
        assertEquals(order, connector.getApplied("resource"));
        assertEquals(
            "URL Sent",
            location,
            ((ConnectorOptions) this.connector.getApplied("options")).getURI());
    }

    /**
     * Test so update sets the location when an URI is supplied.
     *
     * @throws URISyntaxException but it doesn't really.
     */
    @Test
    public void testUpdateSetLocation() throws URISyntaxException {
        URI uri = new URI("http://klarna.com/foo/bar/18");

        order.update(this.connector, uri);
        assertEquals("POST", connector.getApplied("method"));
        assertEquals(order, connector.getApplied("resource"));
        assertEquals(
            "URL Sent",
            uri,
            ((ConnectorOptions) this.connector.getApplied("options")).getURI());
        assertEquals(
            "resource location",
            uri,
            order.getLocation());
    }

    /**
     * Test to verify the entry point (Base URI) can be changed.
     *
     * @throws URISyntaxException but it won't.
     */
    @Test
    public void testCreateAlternateEntryPoint() throws URISyntaxException {
        URI base = new URI("https://checkout.klarna.com/beta/checkout/orders");
        Order.baseUri = base;

        Order o = new Order();
        o.create(connector);
        assertEquals(
            "New Base",
            base,
            ((ConnectorOptions) this.connector.getApplied("options"))
                .getURI());
    }
}