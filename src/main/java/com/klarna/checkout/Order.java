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
 * File containing the Order object
 */
package com.klarna.checkout;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the Resource interface.
 */
public class Order implements IResource {

    /**
     * Connector.
     */
    protected final IConnector connector;
    /**
     * The location of the resource.
     */
    private URI location;
    /**
     * Data of the order.
     */
    private final Map data;
    /**
     * Base URI used to create order resources.
     */
    private static URI baseUri = null;
    /**
     * Content type string.
     */
    private static String contentType = "";

    /**
     * @return the set BaseURI
     */
    public static URI getBaseUri() {
        return Order.baseUri;
    }

    /**
     * Set a base URI.
     *
     * @param uri URI to set
     */
    public static void setBaseUri(final URI uri) {
        Order.baseUri = uri;
    }

    /**
     * Set the content type to be used.
     *
     * @param type content type to set
     */
    public static void setContentType(final String type) {
        Order.contentType = type;
    }

    /**
     * Constructor.
     *
     * @param conn IConnector implementation
     */
    public Order(final IConnector conn) {
        this(conn, null);
    }

    /**
     * Constructor.
     *
     * @param conn IConnector implementation
     * @param uri URI
     */
    public Order(final IConnector conn, final URI uri) {
        this.data = new HashMap<String, Object>();
        this.connector = conn;
        if (uri != null) {
            this.location = uri;
        }
    }

    /**
     * @return the URL of the resource.
     */
    @Override
    public URI getLocation() {
        return location;
    }

    /**
     * Set the URL of the resource.
     *
     * @param uri URI object pointing to the resource
     */
    @Override
    public void setLocation(final URI uri) {
        this.location = uri;
    }

    /**
     * Return content type of the resource.
     *
     * @return Content type
     */
    @Override
    public String getContentType() {
        return Order.contentType;
    }

    /**
     * Update resource with new data.
     *
     * @param newData new data to update the resource with
     */
    @Override
    public void parse(final Map<String, Object> newData) {
        this.data.clear();
        this.data.putAll(newData);
    }

    /**
     * @return Basic representation of the object.
     */
    @Override
    public Map marshal() {
        return this.data;
    }

    /**
     * Get an item from the data object.
     *
     * @param key key to get
     *
     * @return object matching the key.
     */
    public Object get(final String key) {
        return this.data.get(key);
    }

    /**
     * Create a new order.
     *
     * @see Order#baseUri
     *
     * @param datum Data to create with
     *
     * @throws IOException in case of an I/O error
     */
    public void create(final Map<String, Object> datum)
            throws IOException {
        ConnectorOptions options = new ConnectorOptions();
        options.setURI(Order.baseUri);
        options.setData(datum);

        connector.apply("POST", this, options);
    }

    /**
     * Fetch order data.
     *
     * @throws IOException in case of an I/O error
     */
    public void fetch() throws IOException {
        ConnectorOptions options = new ConnectorOptions();
        options.setURI(this.location);

        connector.apply("GET", this, options);
    }

    /**
     * Update order data.
     *
     * @param datum Data to create with
     *
     * @throws IOException in case of an I/O error
     */
    public void update(final Map<String, Object> datum) throws IOException {
        ConnectorOptions options = new ConnectorOptions();
        options.setURI(this.location);
        options.setData(datum);

        connector.apply("POST", this, options);
    }
}
