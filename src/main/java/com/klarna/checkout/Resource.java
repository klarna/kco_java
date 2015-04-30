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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Base implementation of resource interface.
 */
abstract class Resource implements IResource {

    /**
     * Content type string.
     */
    protected String contentType;

    /**
     * Accept type string.
     */
    protected String accept;

    /**
     * Connector.
     */
    protected final IConnector connector;

    /**
     * Data of the order.
     */
    protected final Map data;

    /**
     * Resource location.
     */
    protected URI location;

    /**
     * Constructor.
     *
     * @param conn IConnector implementation
     */
    public Resource(final IConnector conn) {
        this(conn, null);
    }

    /**
     * Constructor.
     *
     * @param conn IConnector implementation
     * @param uri  URI
     */
    public Resource(final IConnector conn, final URI uri) {
        this.data = new HashMap<String, Object>();
        this.connector = conn;

        if (uri != null) {
            this.setLocation(uri);
        }
    }

    @Override
    public void setContentType(final String type) {
        this.contentType = type;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public void setAccept(final String type) {
        this.accept = type;
    }

    @Override
    public String getAccept() {
        return this.accept;
    }

    @Override
    public URI getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(final URI uri) {
        this.location = uri;
    }

    @Override
    public void parse(final Map<String, Object> newData) {
        this.data.clear();
        this.data.putAll(newData);
    }

    @Override
    public Map marshal() {
        return this.data;
    }

    /**
     * Get an item from the data object.
     *
     * @param key key to get
     * @return object matching the key.
     */
    public Object get(final String key) {
        return this.data.get(key);
    }
}
