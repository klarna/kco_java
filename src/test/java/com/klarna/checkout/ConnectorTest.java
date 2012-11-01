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
 * File containing the unit tests for BasicConnector.
 */
package com.klarna.checkout;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * General Unit Tests for Connector class.
 */
public class ConnectorTest {

    /**
     * Resource mock.
     */
    private IResource resource;
    /**
     * Connector object.
     */
    private BasicConnector conn;
    /**
     * Digest mock.
     */
    private Digest digest;

    /**
     * Set up tests.
     */
    @Before
    public void setUp() {
        this.resource = mock(IResource.class);
        this.digest = mock(Digest.class);
        this.conn = new BasicConnector(this.digest);

        when(this.digest.create(anyString())).thenReturn("bob");
    }

    /**
     * Test invalid HTTP Method.
     *
     * @throws Exception as expected
     */
    @Test(expected = IllegalArgumentException.class)
    public void testApplyInvalidMethod() throws Exception {
        conn.apply("ABLORG", this.resource, new ConnectorOptions());
    }

    /**
     * Test null resource.
     *
     * @throws Exception as expected
     */
    @Test(expected = IllegalArgumentException.class)
    public void testApplyNullResource() throws Exception {
        conn.apply("GET", null, new ConnectorOptions());
    }

    /**
     * Test of create method, of the Connector factory.
     *
     * @throws Exception if the JVM doesn't support SHA-256.
     */
    @Test
    public void testCreate() throws Exception {
        IConnector result = Connector.create("sharedSecret");

        assertThat(
                result,
                org.hamcrest.Matchers.instanceOf(IConnector.class));

        assertThat(
                result,
                org.hamcrest.Matchers.instanceOf(BasicConnector.class));
    }
}
