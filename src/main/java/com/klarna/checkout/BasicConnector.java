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
 * File containing the BasicConnector class.
 */
package com.klarna.checkout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONObject;

/**
 * Implementation of the connector interface.
 */
public class BasicConnector implements IConnector {

    /**
     * HttpClient implementation.
     */
    protected IHttpClient client;
    /**
     * Digest instance.
     */
    protected final Digest digest;

    /**
     * Constructor.
     *
     * @param dig Digest instance
     */
    public BasicConnector(final Digest dig) {
        if (dig == null) {
            throw new IllegalArgumentException(
                    "Digest may not be null.");
        }

        this.digest = dig;
    }

    /**
     * Create a HTTP Client.
     *
     * @return HTTP Client to use.
     */
    protected IHttpClient createHttpClient() {
        BasicHttpParams params = new BasicHttpParams();
        params.setParameter("http.protocol.allow-circular-redirects", false);
        return new HttpClientWrapper(
                new BasicClientConnectionManager(), params);
    }

    /**
     * Applying the method on the specific resource. No Options.
     *
     * @param method HTTP method
     * @param resource Resource implementation
     *
     * @return A HttpResponse object
     *
     * @throws IOException In case of an I/O Error.
     */
    @Override
    public HttpResponse apply(final String method, final IResource resource)
            throws IOException {
        return apply(method, resource, new ConnectorOptions());
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
        if (resource == null) {
            throw new IllegalArgumentException(
                    "IResource implementation may not be null.");
        }

        if (!method.equals("GET") && !method.equals("POST")) {
            throw new IllegalArgumentException(
                    "Unsupported HTTP Method. (" + method + ")");
        }

        HttpUriRequest req = createRequest(method, resource, options);

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
     * Get the data to use.
     *
     * @param options Options for the Connector
     * @param resource IResource implementation
     *
     * @return Data to use
     */
    protected Map<String, Object> getData(
            final ConnectorOptions options, final IResource resource) {
        if (options.getData() != null) {
            return options.getData();
        }
        return resource.marshal();
    }

    /**
     * Create a HttpUriRequest object.
     *
     * @param method HTTP Method
     * @param resource IResource implementation
     * @param options Options for Connector
     *
     * @return the appropriate HttpUriRequest
     *
     * @throws UnsupportedEncodingException if the payloads encoding is not
     * supported
     */
    protected HttpUriRequest createRequest(
            final String method, final IResource resource,
            final ConnectorOptions options)
            throws UnsupportedEncodingException {

        URI uri = this.getUri(options, resource);

        HttpUriRequest req;

        if (method.equals("GET")) {
            req = new HttpGet(uri);
        } else {
            HttpPost post = new HttpPost(uri);
            String payload = JSONObject.toJSONString(
                    getData(options, resource));
            post.setEntity(new StringEntity(payload));

            post.setHeader("Content-Type", resource.getContentType());
            req = post;
        }

        req.setHeader("UserAgent", createtUserAgent().toString());
        req.setHeader("Accept", resource.getContentType());

        return req;
    }

    /**
     * Creates a new UserAgent.
     *
     * @return User Agent information
     */
    protected UserAgent createtUserAgent() {
        return new UserAgent();
    }

    /**
     * Get a HttpClient object. Reuse existing if possible.
     *
     * @return A new or the existing HttpClient object.
     */
    public IHttpClient getClient() {
        if (this.client == null) {
            this.client = this.createHttpClient();
            this.client.addResponseInterceptor(
                    new ResourceLocationInterceptor());
            this.client.addRequestInterceptor(
                    new AuthorizationInterceptor(this.digest));
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
}
