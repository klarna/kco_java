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
import java.net.URISyntaxException;

/**
 * Recurring status resource.
 */
public class RecurringStatus extends Resource implements IFetchable {

    /**
     * Resource path.
     */
    public static final String PATH = "/checkout/recurring/TOKEN";

    /**
     * Constructor.
     *
     * @param conn  IConnector implementation
     * @param token Recurring order token
     * @throws URISyntaxException If URI wasn't created correctly.
     */
    public RecurringStatus(final IConnector conn, final String token)
            throws URISyntaxException {
        super(conn, new URI(
                conn.getBaseUri().concat(PATH.replace("TOKEN", token))));
        this.setContentType(
                "application/vnd.klarna.checkout.recurring-status-v1+json");
        this.setAccept(this.getContentType());
    }

    @Override
    public void fetch() throws IOException, UnsupportedOperationException {
        ConnectorOptions options = new ConnectorOptions();

        options.setURI(this.getLocation());

        connector.apply("GET", this, options);
    }
}
