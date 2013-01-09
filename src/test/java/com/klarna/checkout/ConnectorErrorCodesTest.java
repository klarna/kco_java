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
 * File containing the unit tests for BasicConnector error codes.
 */
package com.klarna.checkout;

import com.klarna.checkout.stubs.HttpClientStub;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import org.apache.http.client.HttpResponseException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.mockito.Mockito.*;

/**
 * Parameterized tests for error codes.
 */
@RunWith(value = Parameterized.class)
public class ConnectorErrorCodesTest {

    /**
     * Exception message.
     */
    private final String message;
    /**
     * Status code.
     */
    private int code;
    /**
     * IHttpClient Stub.
     */
    private HttpClientStub transport;
    /**
     * Connector object.
     */
    private BasicConnector conn;
    /**
     * Expected digest string.
     */
    private String digestString;
    /**
     * Digest mock.
     */
    private Digest digest;

    /**
     * Constructor. Needed for parameterized running.
     *
     * @param statusCode status code
     * @param exceptionMessage exception message
     */
    public ConnectorErrorCodesTest(
            final int statusCode, final String exceptionMessage) {
        this.code = statusCode;
        this.message = exceptionMessage;
    }

    /**
     * Set up tests.
     *
     * @throws UnsupportedEncodingException if UTF-8 is not supported.
     */
    @Before
    public void setUp() throws UnsupportedEncodingException {
        this.transport = new HttpClientStub();
        this.digest = mock(Digest.class);
        this.digestString = "stnaeu\\eu2341aoaaoae==";
        when(digest.create("secret")).thenReturn(digestString);
        this.conn = new BasicConnector(this.digest) {
            @Override
            protected IHttpClient createHttpClient() {
                return transport;
            }
        };
    }

    /**
     * @return test parameters
     */
    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
            {400, "Bad Request"},
            {401, "Unauthorized"},
            {402, "PaymentRequired"},
            {403, "Forbidden"},
            {404, "Not Found"},
            {406, "HTTP Error"},
            {409, "HTTP Error"},
            {412, "HTTP Error"},
            {415, "HTTP Error"},
            {422, "HTTP Error"},
            {428, "HTTP Error"},
            {429, "HTTP Error"},
            {500, "Internal Server Error"},
            {502, "Service temporarily overloaded"},
            {503, "Gateway timeout"}
        };
        return Arrays.asList(data);
    }
    /**
     * Exception rule.
     */
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Test error codes with GET.
     *
     * @throws Exception as it should.
     */
    @Test
    public void testGetErrorCode() throws Exception {
        expectedEx.expect(HttpResponseException.class);
        expectedEx.expectMessage(this.message);

        when(digest.create("secret")).thenReturn(digestString);
        IResource resource = mock(IResource.class);
        transport.addResponse(this.code, this.message);

        final URI expectedUri = new URI("http://www.foo.bar");

        conn.apply(
                "GET", resource, new ConnectorOptions() {
            {
                setURI(expectedUri);
            }
        });

        assertEquals("GET", transport.getHttpUriRequest().getMethod());
        assertEquals(expectedUri, transport.getHttpUriRequest().getURI());
    }

    /**
     * Test Error Codes with POST.
     *
     * @throws Exception as it should.
     */
    @Test
    public void testPostErrorCode() throws Exception {
        expectedEx.expect(HttpResponseException.class);
        expectedEx.expectMessage(this.message);

        IResource resource = mock(IResource.class);
        transport.addResponse(this.code, this.message);

        final URI expectedUri = new URI("http://www.foo.bar");

        conn.apply(
                "POST", resource, new ConnectorOptions() {
            {
                setURI(expectedUri);
            }
        });

        assertEquals("POST", transport.getHttpUriRequest().getMethod());
        assertEquals(expectedUri, transport.getHttpUriRequest().getURI());
    }
}
