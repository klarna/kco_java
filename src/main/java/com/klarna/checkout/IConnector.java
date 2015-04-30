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

import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Interface for the Connector object.
 */
public interface IConnector {

    /**
     * Live host.
     */
    String BASE_URL = "https://checkout.klarna.com";

    /**
     * Test host.
     */
    String TEST_BASE_URL = "https://checkout.testdrive.klarna.com";

    /**
     * Get connector base URI string.
     *
     * @return Connector Base URI string
     */
    String getBaseUri();

    /**
     * Set connector base URI string.
     *
     * @param uri Connector Base URI string
     */
    void setBaseUri(final String uri);

    /**
     * Applying the method on the specific resource.
     *
     * @param method   HTTP Method
     * @param resource IResource implementation
     * @param options  Options for Connector
     * @return a HTTP Response
     * @throws IOException in case of an I/O Error
     */
    HttpResponse apply(
            String method, IResource resource, ConnectorOptions options)
            throws IOException;

    /**
     * Applying the method on the specific resource. No options.
     *
     * @param method   HTTP Method
     * @param resource IResource implementation
     * @return a HTTP Response
     * @throws IOException in case of an I/O Error
     */
    HttpResponse apply(
            String method, IResource resource)
            throws IOException;

    /**
     * Specify a socket timeout to use.
     *
     * @param milliseconds Milliseconds to use as timeout.
     */
    void setTimeout(int milliseconds);
}
