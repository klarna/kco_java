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

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

/**
 * Empty class to enforce Interface.
 */
public class HttpClientWrapper extends DefaultHttpClient
    implements IHttpClient {

    public HttpClientWrapper(HttpParams params) {
        super(params);
    }

    public HttpClientWrapper(
            ClientConnectionManager basicClientConnectionManager,
            HttpParams params) {
        super(basicClientConnectionManager, params);
    }

}
