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

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the RecurringOrderTest class, basic functionality.
 */
public class RecurringOrderTest {
    /**
     * Connector mock.
     */
    private IConnector conn;

    /**
     * Set up for tests.
     */
    @Before
    public void setUp() {
        conn = mock(IConnector.class);
        when(conn.getBaseUri()).thenReturn("https://mock.com");
    }

    /**
     * Test that location is initialized when using a token in the constructor.
     *
     * @throws URISyntaxException But not really
     */
    @Test
    public void testLocation() throws URISyntaxException {
        RecurringOrder recurringOrder = new RecurringOrder(conn, "ABC-123");

        assertEquals(
                "Location should be set from constructor with token",
                "https://mock.com/checkout/recurring/ABC-123/orders",
                recurringOrder.getLocation().toString());
    }

    /**
     * Ensure that the location can be set from the constructor.
     *
     * @throws URISyntaxException But not really
     */
    @Test
    public void testLocationWithFullURI() throws URISyntaxException {
        URI uri = new URI("http://example.com");
        RecurringOrder recurringOrder = new RecurringOrder(conn, uri);

        assertEquals(
                "Location should be set from constructor with full uri",
                uri,
                recurringOrder.getLocation());
    }
}
