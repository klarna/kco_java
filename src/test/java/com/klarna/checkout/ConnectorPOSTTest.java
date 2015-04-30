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

import com.klarna.checkout.stubs.HttpClientStub;
import com.klarna.checkout.stubs.HttpClientStub.HTTPResponseStub;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * General Unit Tests for Connector class.
 */
public class ConnectorPOSTTest {

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
     * Connector object.
     */
    private BasicConnector conn;

    /**
     * Set up tests.
     *
     * @throws UnsupportedEncodingException if UTF-8 is not supported.
     */
    @Before
    public void setUp() throws UnsupportedEncodingException {
        this.transport = new HttpClientStub();
        this.resource = mock(IResource.class);
        this.payloadJson = "{\"flobadob\":[\"bobcat\",\"wookie\"]}";
        this.digest = mock(Digest.class);

        String digestString = "stnaeu\\eu2341aoaaoae==";
        when(this.digest.create(anyString())).thenReturn(digestString);
        this.conn = new BasicConnector(this.digest) {
            @Override
            protected IHttpClient createHttpClient() {
                return transport;
            }
        };
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
     * Test for POST call with a 200 response.
     *
     * @throws Exception won't happen.
     */
    @Test
    public void testApplyPost200() throws Exception {
        this.transport.addResponse(
                new HTTPResponseStub(
                        200, new HashMap<String, String>(), payloadJson));

        when(this.resource.marshal()).thenReturn(payloadMap);

        HttpResponse result = conn.apply("POST", resource);

        assertEquals(
                "Payload consumed",
                0,
                result.getEntity().getContent().available());

        verify(this.resource, times(1)).parse(anyMap());
    }

    /**
     * Test to ensure URI sent in with ConnectorOptions is picked up.
     *
     * @throws Exception but no
     */
    @Test
    public void testApplyPostWithURIInOptions() throws Exception {
        transport.addResponse(200, payloadJson);
        final URI expectedUri = new URI("http://www.foo.bar");
        conn.apply(
                "POST", resource, new ConnectorOptions() {
                    {
                        setURI(expectedUri);
                    }
                });

        assertEquals(expectedUri, transport.getHttpUriRequest().getURI());
    }

    /**
     * Test to verify a Post with 303 result redirects to a GET.
     *
     * @throws Exception but it won't
     */
    @Test
    public void testApplyPost303ConvertedToGet() throws Exception {
        ConnectorOptions options = new ConnectorOptions();
        options.setURI("http://localhost");

        transport.addResponse(
                new HTTPResponseStub(
                        303, new HashMap<String, String>() {
                    {
                        put("Location", "herp");
                    }
                }, payloadJson));

        transport.addResponse(
                new HTTPResponseStub(
                        200, new HashMap<String, String>(), payloadJson));

        when(resource.marshal()).thenReturn(payloadMap);

        conn.apply("POST", resource, options);

        verify(digest, times(1)).create(any(InputStream.class));
        verify(digest, times(1)).create(eq(""));
        verify(resource, never()).setLocation(any(URI.class));

        assertEquals(
                "POST changed to GET",
                "GET",
                transport.getHttpUriRequest().getMethod());
    }

    /**
     * Test to ensure a redirect to an error code throws an exception as it
     * should.
     *
     * @throws Exception as it should.
     */
    @Test
    public void testApplyPost303to503() throws Exception {
        ConnectorOptions options = new ConnectorOptions() {
            {
                setURI("http://localhost");
            }
        };
        final URI redirect = new URI("http://not-localhost");

        transport.addResponse(
                new HTTPResponseStub(
                        303, new HashMap<String, String>() {
                    {
                        put("Location", redirect.toString());
                    }
                }, payloadJson));

        transport.addResponse(
                new HTTPResponseStub(
                        503, new HashMap<String, String>(), ""));

        Exception e = null;
        try {
            conn.apply("POST", resource, options);
        } catch (Exception ex) {
            e = ex;
        }

        assertThat(
                e,
                org.hamcrest.Matchers.instanceOf(HttpResponseException.class));
        assertThat(
                transport.getHttpUriRequest().getURI(),
                org.hamcrest.Matchers.is(redirect));
    }

    /**
     * Test to ensure POST with a 201 response updates location.
     *
     * @throws Exception but if it does you're in trouble.
     */
    @Test
    public void testApplyPost201UpdatedLocation() throws Exception {
        final URI newLocation = new URI("http://new_location/");

        transport.addResponse(
                new HTTPResponseStub(
                        201, new HashMap<String, String>() {
                    {
                        put("Location", newLocation.toString());
                    }
                }, ""));
        ConnectorOptions options = new ConnectorOptions() {
            {
                setURI("http://localhost");
            }
        };

        conn.apply("POST", resource, options);

        verify(resource, times(1)).setLocation(newLocation);
    }
}
