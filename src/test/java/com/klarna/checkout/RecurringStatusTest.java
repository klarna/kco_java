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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the RecurringStatus class, basic functionality.
 */
public class RecurringStatusTest {

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
        when(conn.getBaseUri()).thenReturn("https://test.com");
    }

    /**
     * Test that location is initialized when using a token in the constructor.
     *
     * @throws URISyntaxException But not really
     */
    @Test
    public void testLocation() throws URISyntaxException {
        RecurringStatus recurringStatus = new RecurringStatus(conn, "ABC-123");

        assertEquals(
                "Location should be set from constructor with token",
                "https://test.com/checkout/recurring/ABC-123",
                recurringStatus.getLocation().toString());
    }
}
