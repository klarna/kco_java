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

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the Order class, basic functionality.
 */
public class OrderTest {

    /**
     * Connector mock.
     */
    private IConnector conn;

    /**
     * Set up tests.
     */
    @Before
    public void setUp() {
        conn = mock(IConnector.class);
        when(conn.getBaseUri()).thenReturn("https://stubs.com");
    }

    /**
     * Test that location is initialized as null.
     *
     * @throws URISyntaxException But no
     */
    @Test
    public void testLocationEmpty() throws URISyntaxException {
        Order order = new Order(conn);

        assertNull(order.getLocation());
    }

    /**
     * Ensure that the location can be initialized.
     *
     * @throws URISyntaxException But no
     */
    @Test
    public void testLocationURI() throws URISyntaxException {
        Order order = new Order(conn, "123");

        assertEquals(
                "Location should have been set from the constructor",
                "https://stubs.com/checkout/orders/123",
                order.getLocation().toString());
    }
}
