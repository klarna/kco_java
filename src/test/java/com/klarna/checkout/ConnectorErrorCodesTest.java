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
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Parameterized tests for error codes.
 */
@RunWith(value = Parameterized.class)
public class ConnectorErrorCodesTest {

    /**
     * Exception status line.
     */
    private final StatusLine statusLine;

    /**
     * Exception payload.
     */
    private final String payload;

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
     * @param line        Status code & reason phrase
     * @param jsonPayload JSON response
     */
    public ConnectorErrorCodesTest(
            final StatusLine line,
            final String jsonPayload) {
        this.statusLine = line;
        this.payload = jsonPayload;
    }

    /**
     * Test case parameters.
     *
     * @return test parameters
     */
    @Parameters
    public static Collection<Object[]> data() {
        ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
        Object[][] data = new Object[][]{
                {
                        new BasicStatusLine(version, 400, "Bad Request"),
                        "{\"derp\":\"flerp\"}"
                },
                {
                        new BasicStatusLine(version, 401, "Unauthorized"),
                        "{\"some\":\"thing\"}"
                },
                {
                        new BasicStatusLine(version, 402, "PaymentRequired"),
                        "{\"zoid\":\"berg\"}"
                },
                {
                        new BasicStatusLine(version, 403, "Forbidden"),
                        "{\"hammer\":\"time\"}"
                },
                {
                        new BasicStatusLine(version, 404, "Not Found"),
                        "{\"fish\":\"tank\"}"
                },
                {
                        new BasicStatusLine(version, 406, "Not Acceptable"),
                        "{\"sand\":\"wich\"}"
                },
                {
                        new BasicStatusLine(version, 409, "Conflict"),
                        "{\"what\":\"ever\"}"
                },
                {
                        new BasicStatusLine(version, 412, "Precondition Failed"),
                        "{\"win\":\"dows\"}"
                },
                {
                        new BasicStatusLine(version, 415, "Unsupported Media Type"),
                        "{\"bo\":\"ring\"}"
                },
                {
                        new BasicStatusLine(version, 422, "HTTP Error"),
                        "{\"no\":\"op\"}"
                },
                {
                        new BasicStatusLine(version, 428, "HTTP Error"),
                        "{\"need\":\"coffee\"}"
                },
                {
                        new BasicStatusLine(version, 429, "HTTP Error"),
                        "{\"much\":\"better\"}"
                },
                {
                        new BasicStatusLine(version, 500, "Internal Server Error"),
                        "{\"are\":\"we\"}"
                },
                {
                        new BasicStatusLine(version, 502, "Bad Gateway"),
                        "{\"there\":\"yet\"}"
                },
                {
                        new BasicStatusLine(version, 503, "Service Unavailable"),
                        ""
                }
        };
        return Arrays.asList(data);
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
     * Test error codes with GET.
     *
     * @throws Exception as it should.
     */
    @Test
    public void testGetErrorCode() throws Exception {
        when(digest.create("secret")).thenReturn(digestString);
        IResource resource = mock(IResource.class);
        transport.addResponse(statusLine, payload);

        final URI expectedUri = new URI("http://www.foo.bar");

        try {
            conn.apply("GET", resource, new ConnectorOptions() {
                {
                    setURI(expectedUri);
                }
            });
        } catch (ErrorResponseException e) {
            assertEquals(payload, e.getJson().toString());
        } catch (HttpResponseException e) {
            assertEquals(statusLine.getReasonPhrase(), e.getMessage());
        }

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
        IResource resource = mock(IResource.class);

        transport.addResponse(statusLine, payload);

        final URI expectedUri = new URI("http://www.foo.bar");

        try {
            conn.apply("POST", resource, new ConnectorOptions() {
                {
                    setURI(expectedUri);
                }
            });
        } catch (ErrorResponseException e) {
            assertEquals(payload, e.getJson().toString());
        } catch (HttpResponseException e) {
            assertEquals(statusLine.getReasonPhrase(), e.getMessage());
        }

        assertEquals("POST", transport.getHttpUriRequest().getMethod());
        assertEquals(expectedUri, transport.getHttpUriRequest().getURI());
    }
}
