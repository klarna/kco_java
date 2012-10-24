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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * ConnectorOptions holds the options needed for the Connector.
 */
public class ConnectorOptions {

    /**
     * URI option.
     */
    private URI uri;

    /**
     * Set a new URI object.
     *
     * @param newUri new URI to set
     */
    public void setURI(URI newUri) {
        this.uri = newUri;
    }

    /**
     * Set the URI from a string.
     *
     * @param newUri string to parse for new URI location
     *
     * @throws URISyntaxException if newUri could not be parsed.
     */
    public void setURI(String newUri) throws URISyntaxException {
        this.uri = new URI(newUri);
    }

    /**
     * @return the set URI.
     */
    URI getURI() {
        return this.uri;
    }
}