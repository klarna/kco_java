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

import com.klarna.checkout.stubs.ConnectorStub;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the RecurringStatus class, interactions with connector.
 */
public class RecurringStatusWithConnectorTest {

    /**
     * Connector Stub.
     */
    private ConnectorStub connector;

    /**
     * RecurringStatus object.
     */
    private RecurringStatus recurringStatus;

    /**
     * Set up the tests.
     *
     * @throws URISyntaxException But not really
     */
    @Before
    public void setUp() throws URISyntaxException {
        connector = new ConnectorStub() {
            {
                setBaseUri("http://test.com");
            }
        };
        recurringStatus = new RecurringStatus(connector, "15");
        recurringStatus.setContentType("");
    }

    /**
     * Test fetch.
     *
     * @throws Exception if something really weird is going on.
     */
    @Test
    public void testFetch() throws Exception {
        recurringStatus.fetch();

        assertEquals("GET", connector.getApplied("method"));
        assertEquals(recurringStatus, connector.getApplied("resource"));
        ConnectorOptions options = (
                (ConnectorOptions) connector.getApplied("options"));
        assertEquals(
                "http://test.com/checkout/recurring/15",
                options.getURI().toString());
    }
}
