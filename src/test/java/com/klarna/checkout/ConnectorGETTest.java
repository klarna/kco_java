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
 * File containing the ConnectorOptions class.
 */
package com.klarna.checkout;

import com.klarna.checkout.stubs.HttpClientStub;
import com.klarna.checkout.stubs.HttpClientStub.HTTPResponseStub;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * General Unit Tests for Connector class.
 */
public class ConnectorGETTest {

    /**
     * Resource mock.
     */
    private IResource resource;
    /**
     * Payload Map object.
     */
    private Map<String, Object> payloadMap;
    /**
     * Payload JSON string.
     */
    private String payloadJson;
    /**
     * Stubbed transport.
     */
    private HttpClientStub transport;
    /**
     * Digest mock.
     */
    private Digest digest;
    /**
     * Expected digest string.
     */
    private String digestString;
    /**
     * Connector object.
     */
    private Connector conn;

    /**
     * Set up tests.
     */
    @Before
    public void setUp() {
        this.transport = new HttpClientStub();
        this.resource = mock(IResource.class);
        this.payloadJson = "{\"flobadob\":[\"bobcat\", \"wookie\"]}";
        this.digest = mock(Digest.class);
        this.digestString = "stnaeu\\eu2341aoaaoae==";
        this.conn = new Connector(this.digest) {
            @Override
            protected IHttpClient createHttpClient() {
                return transport;
            }
        };
        when(digest.create(anyString())).thenReturn(digestString);
        this.payloadMap = new HashMap<String, Object>() {
            {
                put("flobadob", new ArrayList<String>() {
                    {
                        add("bobcat");
                        add("wookie");
                    }
                });
            }
        };
    }

    /**
     * Test to ensure the resource parses the payload as expected.
     *
     * @throws Exception but not really
     */
    @Test
    public void testApplyGet200() throws Exception {

        transport.addResponse(
                new HTTPResponseStub(
                200, new HashMap<String, String>(), payloadJson));

        HttpResponse result = conn.apply("GET", this.resource, null);

        assertEquals(
                "HTTP Response Code",
                200,
                result.getStatusLine().getStatusCode());

        assertEquals("GET", this.transport.getHttpUriRequest().getMethod());

        verify(this.resource).parse(this.payloadMap);
    }

    /**
     * Test to ensure URI sent in with ConnectorOptions is picked up.
     *
     * @throws Exception but no
     */
    @Test
    public void testApplyGetWithURIInOptions() throws Exception {
        transport.addResponse(
                new HTTPResponseStub(
                200, new HashMap<String, String>(), payloadJson));
        final URI expectedUri = new URI("http://www.foo.bar");

        conn.apply(
                "GET", resource, new ConnectorOptions() {
            {
                setURI(expectedUri);
            }
        });

        assertEquals(expectedUri, transport.getHttpUriRequest().getURI());
    }

    /**
     * Test to verify behavior when getting a 301 redirect to a 200.
     *
     * @throws Exception but it won't
     */
    @Test
    public void testApplyGet301to200() throws Exception {
        ConnectorOptions options = new ConnectorOptions() {
            {
                setURI("http://localhost");
            }
        };
        final URI redirect = new URI("http://not-localhost");

        transport.addResponse(
                new HTTPResponseStub(301, new HashMap<String, String>() {
            {
                put("Location", redirect.toString());
            }
        }, payloadJson));

        transport.addResponse(
                new HTTPResponseStub(
                200, new HashMap<String, String>(), payloadJson));

        conn.apply("GET", resource, options);

        assertEquals(
                redirect,
                transport.getHttpUriRequest().getURI());
    }

    /**
     * Test to verify behavior when getting a 301 redirect to a 200.
     *
     * @throws Exception but it won't
     */
    @Test(expected = HttpResponseException.class)
    public void testApplyGet301to503() throws Exception {
        ConnectorOptions options = new ConnectorOptions() {
            {
                setURI("http://localhost");
            }
        };
        final URI redirect = new URI("http://not-localhost");

        transport.addResponse(
                new HTTPResponseStub(301, new HashMap<String, String>() {
            {
                put("Location", redirect.toString());
            }
        }, payloadJson));

        transport.addResponse(
                new HTTPResponseStub(
                503, new HashMap<String, String>(), payloadJson));

        conn.apply("GET", resource, options);

        assertEquals(
                redirect,
                transport.getHttpUriRequest().getURI());
    }

    /**
     * Test to avoid redirect loop.
     *
     * @throws Exception but not really
     */
    @Test(expected = ClientProtocolException.class)
    public void testApplyGetInfiniteLoop() throws Exception {
        ConnectorOptions options = new ConnectorOptions() {
            {
                setURI("http://localhost");
            }
        };
        final URI redirect = new URI("http://not-localhost");

        transport.addResponse(
                new HTTPResponseStub(301, new HashMap<String, String>() {
            {
                put("Location", redirect.toString());
            }
        }, payloadJson));

        transport.addResponse(
                new HTTPResponseStub(301, new HashMap<String, String>() {
            {
                put("Location", redirect.toString());
            }
        }, payloadJson));

        conn.apply("GET", resource, options);
    }

    /**
     * Test to ensure headers are set.
     *
     * @throws Exception should never occur.
     */
    @Test
    public void testHeadersSet() throws Exception {
        String authorization = "Klarna ".concat(digestString);

        String contentType = "klarna/json";

        when(resource.getContentType()).thenReturn(contentType);

        transport.addResponse(
                new HTTPResponseStub(
                200, new HashMap<String, String>(), payloadJson));

        conn.apply("GET", resource, null);
        HttpUriRequest req = transport.getHttpUriRequest();
        assertNotNull(
                "Authorization header set",
                req.getLastHeader("Authorization"));
        assertEquals(
                "Authorization",
                authorization,
                req.getLastHeader("Authorization").getValue());

        assertEquals(
                "Accept header",
                contentType,
                req.getLastHeader("Accept").getValue());

        verify(digest, times(1)).create("");
    }
}
