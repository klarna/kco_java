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

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

/**
 * Empty class to enforce Interface.
 */
public class HttpClientWrapper extends DefaultHttpClient
        implements IHttpClient {

    /**
     * Constructor.
     *
     * @param params HTTP Parameters to set
     */
    public HttpClientWrapper(final HttpParams params) {
        super(params);
    }

    /**
     * Constructor.
     *
     * @param connectionManager A ClientConnectionManager implementation
     * @param params HTTP Parameters to pass along
     */
    public HttpClientWrapper(
            final ClientConnectionManager connectionManager,
            final HttpParams params) {
        super(connectionManager, params);
    }
}
