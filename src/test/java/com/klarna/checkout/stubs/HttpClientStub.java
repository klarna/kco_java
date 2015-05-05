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

package com.klarna.checkout.stubs;

import com.klarna.checkout.IHttpClient;
import org.apache.http.*;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.EntityEnclosingRequestWrapper;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stubbed implementation of IHttpClient.
 */
public class HttpClientStub implements IHttpClient {

    /**
     * Store for ResponseInterceptors.
     */
    private final ArrayList<HttpResponseInterceptor> responseInterceptors;

    /**
     * Store for RequestInterceptors.
     */
    private final ArrayList<HttpRequestInterceptor> requestInterceptors;

    /**
     * HttpParams storage.
     */
    private final HttpParams params;

    /**
     * HttpUriRequest.
     */
    private HttpUriRequest httpUriReq;

    /**
     * A list holding the expected responses.
     */
    private ArrayList<HTTPResponseStub> responseList;

    /**
     * Memory for the latest response seen.
     */
    private HTTPResponseStub lastResponse;

    /**
     * Visited locations.
     */
    private Set<URI> visited;

    /**
     * Expected Location URI.
     */
    private URI expecedLocation;

    /**
     * Data holder.
     */
    private HashMap<String, String> data;

    /**
     * Constructor.
     */
    public HttpClientStub() {
        responseInterceptors = new ArrayList<HttpResponseInterceptor>();
        requestInterceptors = new ArrayList<HttpRequestInterceptor>();
        responseList = new ArrayList<HTTPResponseStub>();
        params = null;
        visited = new HashSet<URI>();
        data = new HashMap<String, String>();
    }

    /**
     * Constructor taking an HttpParams argument.
     *
     * @param parameters HttpParams to send in.
     */
    public HttpClientStub(final HttpParams parameters) {
        this.params = parameters;
        responseInterceptors = new ArrayList<HttpResponseInterceptor>();
        requestInterceptors = new ArrayList<HttpRequestInterceptor>();
        responseList = new ArrayList<HTTPResponseStub>();
        visited = new HashSet<URI>();
    }

    /**
     * Get HTTP URI request.
     *
     * @return the stored HttpUriRequest
     */
    public HttpUriRequest getHttpUriRequest() {
        return httpUriReq;
    }

    /**
     * Set a HttpUriRequest.
     *
     * @param req Item to set.
     */
    public void setHur(final HttpUriRequest req) {
        this.httpUriReq = req;
    }

    /**
     * Basic execution.
     *
     * @param hur HttpUriRequest object
     * @return first element in the lastResponse list.
     * @throws IOException             if an I/O error occurs
     * @throws ClientProtocolException if a client protocol violation happened
     */
    public HttpResponse execute(final HttpUriRequest hur)
            throws IOException, ClientProtocolException {
        this.httpUriReq = hur;
        this.lastResponse = this.getResponse();
        return this.lastResponse;
    }

    /**
     * Unused stub.
     *
     * @return nothing.
     */
    public HttpParams getParams() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unused stub.
     *
     * @return nothing.
     */
    public ClientConnectionManager getConnectionManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unused stub.
     *
     * @param hur HttpUriRequest
     * @param hc  HttpContext
     * @return nothing.
     * @throws IOException             never
     * @throws ClientProtocolException never
     */
    public HttpResponse execute(final HttpUriRequest hur, final HttpContext hc)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unused stub.
     *
     * @param hh HttpHost
     * @param hr HttpRequest
     * @return nothing.
     * @throws IOException             never
     * @throws ClientProtocolException never
     */
    public HttpResponse execute(
            final HttpHost hh, final HttpRequest hr)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unused stub.
     *
     * @param hh HttpHost
     * @param hr HttpRequest
     * @param hc HttpContext
     * @return nothing.
     * @throws IOException             never
     * @throws ClientProtocolException never
     */
    public HttpResponse execute(
            final HttpHost hh, final HttpRequest hr, final HttpContext hc)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Uppercase data to simulate it having passed on to the server.
     *
     * @throws IOException in case of an IO error.
     */
    private void fixData() throws IOException {
        if (this.httpUriReq.getMethod().equals("POST")) {
            HttpEntityEnclosingRequest h =
                    (HttpEntityEnclosingRequest) this.httpUriReq;

            InputStream is = h.getEntity().getContent();
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");

            String str = "";
            while (s.hasNext()) {
                str = str.concat(s.next());
            }
            JSONParser jsonParser = new JSONParser();
            HashMap<String, String> obj = null;
            try {
                obj = (HashMap<String, String>) jsonParser.parse(str);
            } catch (ParseException ex) {
                Logger.getLogger(
                        HttpClientStub.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            if (obj != null && obj.containsKey("test")) {
                str = obj.get("test").toUpperCase();
                this.data.put("test", str);
            }

            ByteArrayInputStream in = new ByteArrayInputStream(
                    JSONObject.toJSONString(this.data).getBytes());

            this.lastResponse.setEntity(
                    new InputStreamEntity(in, in.available()));
        }
    }

    /**
     * Stubbed Execute implementation.
     *
     * @param <T> The class ResponseHandler operates on
     * @param hur HttpUriRequest object
     * @param rh  ResponseHandler object
     * @param hc  HttpContext holder
     * @return ResponseHandler result
     * @throws IOException never
     */
    public <T> T execute(
            final HttpUriRequest hur,
            final ResponseHandler<? extends T> rh,
            final HttpContext hc)
            throws IOException {
        this.httpUriReq = hur;

        List<Integer> redirects = new ArrayList();
        redirects.add(301);
        redirects.add(302);
        redirects.add(303);
        this.visited.clear();
        if (this.httpUriReq instanceof HttpEntityEnclosingRequest) {
            try {
                this.httpUriReq = new EntityEnclosingRequestWrapper(
                        (HttpEntityEnclosingRequest) hur);
            } catch (ProtocolException ex) {
                throw new IOException(ex.getMessage());
            }
        }
        int status;
        do {
            try {
                for (HttpRequestInterceptor hri : requestInterceptors) {
                    hri.process(this.httpUriReq, hc);
                }
            } catch (HttpException ex) {
                throw new ClientProtocolException(ex);
            }

            if (!this.visited.add(this.httpUriReq.getURI())) {
                throw new ClientProtocolException(
                        new CircularRedirectException());
            }

            this.lastResponse = this.getResponse();

            if (this.lastResponse.getStatusLine().getStatusCode() < 400) {
                fixData();
            }

            try {
                for (HttpResponseInterceptor hri : responseInterceptors) {
                    hri.process(this.lastResponse, hc);
                }
            } catch (HttpException ex) {
                throw new ClientProtocolException(ex);
            }
            status = this.lastResponse.getStatusLine().getStatusCode();
            Header location = this.lastResponse.getLastHeader("Location");
            if (location != null) {
                this.httpUriReq = new HttpGet(location.getValue());
            }
        } while (redirects.contains(status));

        if (this.data.containsKey("test")) {
            ByteArrayInputStream bis = new ByteArrayInputStream(
                    JSONObject.toJSONString(data).getBytes());
            this.lastResponse.setEntity(
                    new InputStreamEntity(bis, bis.available()));
        }

        return rh.handleResponse(this.lastResponse);
    }

    /**
     * Unused stub.
     *
     * @param <T> Class
     * @param hh  HttpHost
     * @param hr  HttpRequest
     * @param rh  ResponseHandler
     * @return nothing
     * @throws IOException             never
     * @throws ClientProtocolException never
     */
    public <T> T execute(
            final HttpHost hh,
            final HttpRequest hr,
            final ResponseHandler<? extends T> rh)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unused stub.
     *
     * @param <T> Class
     * @param hh  HttpHost
     * @param hr  HttpRequest
     * @param rh  ResponseHandler
     * @param hc  HttpContext
     * @return nothing
     * @throws IOException             never
     * @throws ClientProtocolException never
     */
    public <T> T execute(
            final HttpHost hh,
            final HttpRequest hr,
            final ResponseHandler<? extends T> rh,
            final HttpContext hc)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Add a response with only a status code.
     *
     * @param i status code
     */
    public void addResponse(final int i) {
        addResponse(new HTTPResponseStub(i, new HashMap<String, String>(), ""));
    }

    /**
     * Add a response with a status code and a payload.
     *
     * @param i   status code
     * @param str payload string
     */
    public void addResponse(final int i, final String str) {
        addResponse(
                new HTTPResponseStub(i, new HashMap<String, String>(), str));
    }

    /**
     * Add a response with a status code, a reason phrase and a payload.
     *
     * @param statusLine Status line
     * @param str        payload string
     */
    public void addResponse(final StatusLine statusLine, final String str) {
        addResponse(
                new HTTPResponseStub(statusLine, new HashMap<String, String>(), str)
        );
    }

    /**
     * Add a complete response.
     *
     * @param httpResponseStub response to set
     */
    public void addResponse(final HTTPResponseStub httpResponseStub) {
        this.responseList.add(httpResponseStub);
    }

    /**
     * Add a Fitnesse response.
     *
     * @param uri     uri
     * @param status  status
     * @param headers headers
     * @throws URISyntaxException if uri could not be parsed.
     */
    public void addResponse(
            final String uri, final int status,
            final Map<String, String> headers)
            throws URISyntaxException {
        this.expecedLocation = new URI(uri);
        addResponse(
                new HTTPResponseStub(status, headers, ""));
    }

    /**
     * Get response.
     *
     * @return the first item in the response list.
     */
    public HTTPResponseStub getResponse() {
        return this.responseList.remove(0);
    }

    /**
     * Unused stub.
     *
     * @param <T> Class
     * @param hur HttpUriRequest
     * @param rh  ResponseHandler
     * @return nothing
     * @throws IOException             never
     * @throws ClientProtocolException never
     */
    public <T> T execute(
            final HttpUriRequest hur,
            final ResponseHandler<? extends T> rh)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Add a responseInterceptor.
     *
     * @param hri responseInterceptor to add
     */
    public void addResponseInterceptor(final HttpResponseInterceptor hri) {
        responseInterceptors.add(hri);
    }

    /**
     * Add a requestInterceptor.
     *
     * @param hri requestInterceptor to add
     */
    public void addRequestInterceptor(final HttpRequestInterceptor hri) {
        requestInterceptors.add(hri);
    }

    /**
     * Stubbed BasicHttpResponse.
     */
    public static class HTTPResponseStub extends BasicHttpResponse {

        /**
         * Constructor.
         *
         * @param code        status code
         * @param reason      reason phrase
         * @param headers     http header map
         * @param payloadJson payload string
         */
        public HTTPResponseStub(
                final int code,
                final String reason,
                final Map<String, String> headers,
                final String payloadJson) {

            super(new BasicStatusLine(
                    new ProtocolVersion("HTTP", 1, 1), code, reason));

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                this.setHeader(entry.getKey(), entry.getValue());
            }

            if (payloadJson != null) {
                try {
                    InputStream is = new ByteArrayInputStream(
                            payloadJson.getBytes("UTF-8"));
                    this.setEntity(new InputStreamEntity(is, is.available()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(
                            HttpClientStub.class.getName()).log(
                            Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(
                            HttpClientStub.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        }

        /**
         * Constructor.
         *
         * @param code        status code
         * @param headers     http header map
         * @param payloadJson payload string
         */
        public HTTPResponseStub(
                final int code,
                final Map<String, String> headers,
                final String payloadJson) {
            this(code, "ok", headers, payloadJson);
        }

        /**
         * Constructor.
         *
         * @param statusLine  Status line
         * @param headers     http header map
         * @param payloadJson payload string
         */
        public HTTPResponseStub(
                final StatusLine statusLine,
                final Map<String, String> headers,
                final String payloadJson) {
            this(
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase(),
                    headers,
                    payloadJson);
        }
    }
}
