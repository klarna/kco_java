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
     * Base URI used to create order resources.
     */
    public static URI baseUri = null;
    /**
     * Content type string.
     */
    public static String contentType = "";
    /**
     * The location of the resource.
     */
    private URI location;
    /**
     * Data of the order.
     */
    private final Map data;
    /**
     * Resource object.
     */
    private IConnector connector;

    /**
     * Constructor.
     *
     * @param conn IConnector implementation.
     */
    public Order(final IConnector conn) {
        this.data = new HashMap<String, Object>();
        if (conn == null) {
            throw new IllegalArgumentException(
                    "IConnector implementation may not be null.");
        }
        this.connector = conn;
    }

    /**
     * Attaches a connector object to the order.
     *
     * @param conn IConnector implementation
     */
    public void setConnector(final IConnector conn) {
        this.connector = conn;
    }

    /**
     * @return attached resource object.
     */
    public IConnector getConnector() {
        return this.connector;
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
     * Set a value on the order.
     *
     * @param key key to set
     * @param value to add
     */
    public void set(final String key, final Object value) {
        this.data.put(key, value);
    }

    /**
     * Create a new order.
     *
     * @see Order#baseUri
     *
     * @throws IOException in case of an I/O error
     */
    public void create()
            throws IOException {
        ConnectorOptions options = new ConnectorOptions();
        options.setURI(Order.baseUri);

        this.connector.apply("POST", this, options);
    }

    /**
     * Fetch order data.
     *
     * @throws IOException in case of an I/O error
     */
    public void fetch() throws IOException {
        ConnectorOptions options = new ConnectorOptions();
        options.setURI(this.location);

        this.connector.apply("GET", this, options);
    }

    /**
     * Fetch order data.
     *
     * @param newLocation URI
     *
     * @throws IOException in case of an I/O error
     */
    public void fetch(final URI newLocation)
            throws IOException {
        this.setLocation(newLocation);
        this.fetch();
    }

    /**
     * Update order data.
     *
     * @throws IOException in case of an I/O error
     */
    public void update() throws IOException {
        ConnectorOptions options = new ConnectorOptions();
        options.setURI(this.location);

        this.connector.apply("POST", this, options);
    }

    /**
     * Update order data.
     *
     * @param newLocation URI to new location.
     *
     * @throws IOException in case of an I/O error
     */
    public void update(final URI newLocation)
            throws IOException {
        this.setLocation(newLocation);
        this.update();
    }
}
