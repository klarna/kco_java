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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the Handler class.
 */
public class HandlerTest {

    /**
     * Logging output stream.
     */
    private OutputStream outputStream;

    /**
     * Logging stream handler.
     */
    private StreamHandler streamHandler;

    /**
     * Set up for tests.
     */
    @Before
    public void setUp() {
        Logger logger = Logger.getLogger(Handler.class.getName());
        outputStream = new ByteArrayOutputStream();
        streamHandler = new StreamHandler(
                outputStream,
                logger.getParent().getHandlers()[0].getFormatter());

        logger.addHandler(streamHandler);
    }

    /**
     * Test of handleResponse.
     *
     * @throws Exception if things go bad.
     */
    @Test
    public void testHandleResponse() throws Exception {
        HttpResponse res = mock(HttpResponse.class);
        Handler handler = new Handler(mock(IResource.class));
        StatusLine sline = mock(StatusLine.class);
        when(sline.getStatusCode()).thenReturn(200);
        when(res.getStatusLine()).thenReturn(sline);
        when(res.getEntity()).thenReturn(new StringEntity("{}"));
        handler.handleResponse(res);
    }

    /**
     * Test of handleResponse with an invalid json response.
     *
     * @throws IOException if things go as planned.
     */
    @Test(expected = IOException.class)
    public void testHandleBadResponse() throws IOException {
        HttpResponse res = mock(HttpResponse.class);
        Handler handler = new Handler(mock(IResource.class));
        StatusLine sline = mock(StatusLine.class);
        when(sline.getStatusCode()).thenReturn(200);
        when(res.getStatusLine()).thenReturn(sline);
        when(res.getEntity()).thenReturn(new StringEntity(""));
        handler.handleResponse(res);
    }

    /**
     * Test of verifyStatusCode method, of class Handler.
     *
     * @throws IOException if test fail
     */
    @Test
    public void testVerifyStatusCodeOK() throws IOException {
        HttpResponse res = mock(HttpResponse.class);
        Handler handler = new Handler(mock(IResource.class));
        StatusLine sline = mock(StatusLine.class);
        when(sline.getStatusCode()).thenReturn(200);
        when(res.getStatusLine()).thenReturn(sline);
        handler.verifyStatusCode(res);
    }

    /**
     * Test of verifyStatusCode method, of class Handler.
     *
     * @throws IOException if test fail
     */
    @Test
    public void testVerifyStatusCodeUnchecked() throws IOException {
        HttpResponse res = mock(HttpResponse.class);
        Handler handler = new Handler(mock(IResource.class));
        StatusLine sline = mock(StatusLine.class);
        when(sline.getStatusCode()).thenReturn(640);
        when(res.getStatusLine()).thenReturn(sline);
        handler.verifyStatusCode(res);
    }

    /**
     * Test of verifyStatusCode method, of class Handler.
     *
     * @throws IOException if test fail
     */
    @Test(expected = HttpResponseException.class)
    public void testVerifyStatusCodeError() throws IOException {
        HttpResponse res = mock(HttpResponse.class);
        Handler handler = new Handler(mock(IResource.class));
        StatusLine sline = mock(StatusLine.class);
        when(sline.getStatusCode()).thenReturn(404);
        when(res.getStatusLine()).thenReturn(sline);
        when(res.getEntity()).thenReturn(new StringEntity("fail"));
        handler.verifyStatusCode(res);
    }

    /**
     * Test of parsePayload method, of class Handler.
     *
     * @throws IOException        if the test fails.
     * @throws URISyntaxException But not really
     */
    @Test
    public void testParsePayload() throws IOException, URISyntaxException {
        String payload = "{\"foo\":\"bar\"}";
        Order o = new Order(mock(IConnector.class));
        Handler instance = new Handler(o);
        instance.parsePayload(payload);
        Map<String, Object> expected;
        expected = new HashMap<String, Object>() {
            {
                put("foo", "bar");
            }
        };
        assertEquals(expected, o.marshal());
    }

    /**
     * Test that parsePayload throws an IOException with an empty payload.
     *
     * @throws IOException if the test passes
     */
    @Test(expected = IOException.class)
    public void testParseEmptyPayload() throws IOException {
        String payload = "";
        Handler instance = new Handler(mock(IResource.class));
        instance.parsePayload(payload);
    }

    /**
     * Ensure that a message is logged when the http entity stream cannot be
     * read.
     *
     * @throws Exception No.
     */
    @Test
    public void testStreamErrorLogging() throws Exception {
        HttpResponse response = mock(HttpResponse.class);
        StatusLine line = mock(StatusLine.class);
        HttpEntity entity = mock(HttpEntity.class);
        Handler handler = new Handler(mock(IResource.class));

        when(line.getStatusCode()).thenReturn(400);
        when(entity.getContent()).thenThrow(new IOException("Boo"));
        when(response.getStatusLine()).thenReturn(line);
        when(response.getEntity()).thenReturn(entity);

        try {
            handler.handleResponse(response);
        } catch (Exception e) {
            // Fall through so we can test the logging.
        }

        streamHandler.flush();

        assertThat(
                outputStream.toString(),
                containsString("SEVERE: Failed to parse response"));
    }

    /**
     * Ensure that a message is logged when the JSON response couldn't be
     * parsed properly.
     *
     * @throws Exception No.
     */
    @Test
    public void testJsonParsingErrorLogging() throws Exception {
        HttpResponse response = mock(HttpResponse.class);
        StatusLine line = mock(StatusLine.class);
        Handler handler = new Handler(mock(IResource.class));

        when(line.getStatusCode()).thenReturn(400);
        when(response.getStatusLine()).thenReturn(line);
        when(response.getEntity()).thenReturn(new StringEntity("{"));

        try {
            handler.handleResponse(response);
        } catch (Exception e) {
            // Fall through so we can test the logging.
        }

        streamHandler.flush();

        assertThat(
                outputStream.toString(),
                containsString("SEVERE: Invalid JSON response"));
    }
}
