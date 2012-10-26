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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONObject;

/**
 * Implementation of the connector interface.
 */
public class Connector implements IConnector {

    /**
     * HttpClient implementation.
     */
    private IHttpClient client;
    /**
     * Digest instance.
     */
    private final Digest digest;

    /**
     * Constructor.
     *
     * @param dig Digest instance
     */
    public Connector(final Digest dig) {
        this.digest = dig;
    }

    /**
     * Create a HTTP Client.
     *
     * @return HTTP Client to use.
     */
    protected IHttpClient createHttpClient() {
        return new HttpClientWrapper();
    }

    /**
     * Applying the method on the specific resource.
     *
     * @param method HTTP method
     * @param resource Resource implementation
     * @param options Connector Options
     *
     * @return A HttpResponse object
     *
     * @throws IOException In case of an I/O Error.
     */
    @Override
    public HttpResponse apply(
            final String method, final IResource resource,
            final ConnectorOptions options)
            throws IOException {
        if (!method.equals("GET") && !method.equals("POST")) {
            throw new IllegalArgumentException("Invalid HTTP Method.");
        }

        URI uri = this.getUri(options, resource);

        HttpUriRequest req = createRequest(method, uri, resource);

        HttpContext ctex = new BasicHttpContext();
        ctex.setAttribute("klarna_resource", resource);
        ctex.setAttribute("klarna_visited", new HashSet<URI>());

        return getClient().execute(req, new Handler(resource), ctex);
    }

    /**
     * Get a usable URI.
     *
     * @param options Options for the Connector
     * @param resource IResource implementation
     *
     * @return URI to use
     */
    protected URI getUri(
            final ConnectorOptions options, final IResource resource) {
        URI uri = null;
        if (options != null) {
            uri = options.getURI();
        }
        if (uri == null) {
            uri = resource.getLocation();
        }
        return uri;
    }

    /**
     * Create a HttpUriRequest object.
     *
     * @param method HTTP Method
     * @param uri Target location
     * @param resource IResource implementation.
     *
     * @return the appropriate HttpUriRequest
     *
     * @throws UnsupportedEncodingException if the payloads encoding is not
     * supported
     */
    protected HttpUriRequest createRequest(
            final String method, final URI uri, final IResource resource)
            throws UnsupportedEncodingException {
        HttpUriRequest req;

        if (method.equals("GET")) {
            req = new HttpGet(uri);
        } else {
            HttpPost post;
            req = post = new HttpPost(uri);
            String payload = JSONObject.toJSONString(resource.marshal());
            post.setEntity(new StringEntity(payload));

            req.setHeader("Content-Type", resource.getContentType());
        }

        req.setHeader("Accept", resource.getContentType());

        return req;
    }

    /**
     * Get a HttpClient object. Reuse existing if possible.
     *
     * @return A new or the existing HttpClient object.
     */
    protected IHttpClient getClient() {
        if (this.client == null) {
            this.client = createHttpClient();
            this.client.addResponseInterceptor(
                    new ResourceLocationInterceptor());
            this.client.addRequestInterceptor(
                    new AuthorizationInterceptor(this.digest));
            this.client.addRequestInterceptor(new RedirectLoopInterceptor());
        }
        return this.client;
    }

    /**
     * An Interceptor to update resource on 201 and 301 statuses.
     */
    private static class ResourceLocationInterceptor
            implements HttpResponseInterceptor {

        /**
         * Process a response, update resource if MOVED_PERMANENTLY or CREATED.
         *
         * @param response A Response from the HTTP request
         * @param context Http Context
         *
         * @throws HttpException If the response location is invalid
         * @throws IOException if an I/O error occurred
         */
        public void process(
                final HttpResponse response, final HttpContext context)
                throws HttpException, IOException {

            IResource resource = (IResource) context.getAttribute(
                    "klarna_resource");

            int code = response.getStatusLine().getStatusCode();
            if (code != HttpStatus.SC_MOVED_PERMANENTLY
                    && code != HttpStatus.SC_CREATED) {
                return;
            }

            try {
                Header headerLocation = response.getLastHeader("Location");
                if (headerLocation != null) {
                    URI location = new URI(headerLocation.getValue());
                    resource.setLocation(location);
                }
            } catch (URISyntaxException ex) {
                throw new ClientProtocolException(ex);
            }
        }
    }

    /**
     * Intercept HTTP Request and add authorization header.
     */
    private static class AuthorizationInterceptor
            implements HttpRequestInterceptor {

        /**
         * Digest object.
         */
        private final Digest digest;

        /**
         * Constructor.
         *
         * @param dig Digester
         */
        public AuthorizationInterceptor(final Digest dig) {
            this.digest = dig;
        }

        /**
         * Set the authorization header.
         *
         * @param request HTTP Request object.
         * @param context HTTP Context holder.
         *
         * @throws HttpException in case of an HTTP protocol violation
         * @throws IOException in case of an I/O error
         */
        public void process(
                final HttpRequest request, final HttpContext context)
                throws HttpException, IOException {
            String digestString;
            if (request instanceof HttpEntityEnclosingRequestBase) {
                HttpEntityEnclosingRequestBase heerb;
                heerb = (HttpEntityEnclosingRequestBase) request;

                digestString = this.digest.create(
                        heerb.getEntity().getContent());
            } else {
                digestString = this.digest.create("");
            }
            request.addHeader("Authorization", "Klarna " + digestString);
        }
    }

    /**
     * Interceptor for redirect loops.
     */
    private static class RedirectLoopInterceptor
            implements HttpRequestInterceptor {

        /**
         * Intercept a redirect loop.
         *
         * @param request HTTP Request object
         * @param context HTTP Context holder
         *
         * @throws HttpException If a loop is detected
         * @throws IOException in case of an I/O error
         */
        @Override
        public void process(
                final HttpRequest request, final HttpContext context)
                throws HttpException, IOException {
            Set<String> visited = (Set<String>) context.getAttribute(
                    "klarna_visited");
            String uri = request.getRequestLine().getUri();
            if (visited.contains(uri)) {
                throw new RedirectException("Infinite redirect loop detected.");
            }
            visited.add(uri);
        }
    }
}
