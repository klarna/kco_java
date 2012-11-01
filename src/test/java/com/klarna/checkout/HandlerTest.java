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
 * File containing unit tests for the Handler class.
 */
package com.klarna.checkout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Handler class.
 */
public class HandlerTest {

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
     * @throws IOException if the test fails.
     */
    @Test
    public void testParsePayload() throws IOException {
        String payload = "{\"foo\":\"bar\"}";
        Order o = new Order();
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
}
