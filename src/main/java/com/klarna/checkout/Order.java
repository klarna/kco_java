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

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Checkout order resource.
 */
public class Order extends Resource
        implements ICreatable, IFetchable, IUpdatable {

    /**
     * Resource path.
     */
    public static final String PATH = "/checkout/orders";

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
     * @param uri  Resource URI
     */
    public Order(final IConnector conn, final URI uri) {
        super(conn, uri);
        this.setContentType(
                "application/vnd.klarna.checkout.aggregated-order-v2+json");
        this.setAccept(this.getContentType());
    }

    @Override
    public void create(final Map<String, Object> datum) throws IOException {
        ConnectorOptions options = new ConnectorOptions();

        options.setURI(URI.create(connector.getBaseUri().concat(PATH)));
        options.setData(datum);

        connector.apply("POST", this, options);
    }

    @Override
    public void fetch() throws IOException {
        ConnectorOptions options = new ConnectorOptions();

        options.setURI(this.getLocation());

        connector.apply("GET", this, options);
    }

    @Override
    public void update(final Map<String, Object> datum) throws IOException {
        ConnectorOptions options = new ConnectorOptions();

        options.setURI(this.getLocation());
        options.setData(datum);

        connector.apply("POST", this, options);
    }
}
