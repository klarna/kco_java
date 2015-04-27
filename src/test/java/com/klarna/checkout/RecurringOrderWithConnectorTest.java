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
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the Order class, interactions with connector.
 */
public class RecurringOrderWithConnectorTest {

    /**
     * Connector Stub.
     */
    private ConnectorStub connector;

    /**
     * Order object.
     */
    private RecurringOrder recurringOrder;

    /**
     * Set up the tests.
     *
     * @throws URISyntaxException But not really
     */
    @Before
    public void setUp() throws URISyntaxException {
        connector = new ConnectorStub();
        recurringOrder = new RecurringOrder(
                connector, new URI("http://example.com/123"));
        recurringOrder.setContentType("");
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

        Map<String, Object> data = new HashMap<String, Object>() {
            {
                put("foo", "boo");
            }
        };

        recurringOrder.create(data);

        assertEquals("POST", connector.getApplied("method"));
        assertEquals(recurringOrder, connector.getApplied("resource"));
        assertEquals(
                "Location should have been updated from stub",
                location,
                recurringOrder.getLocation());
        assertEquals(
                "Data sent",
                data,
                ((ConnectorOptions) connector.getApplied("options")).getData());
    }
}
