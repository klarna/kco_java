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

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;

/**
 * Extension of the org.apache.http.client.HttpClient interface to ensure we
 * have response and request interceptors.
 */
public interface IHttpClient extends HttpClient {

    /**
     * Add a HttpResponseInterceptor.
     *
     * @param hri interceptor implementation
     */
    void addResponseInterceptor(HttpResponseInterceptor hri);

    /**
     * Add a HttpRequestInterceptor.
     *
     * @param hri interceptor implementation
     */
    void addRequestInterceptor(HttpRequestInterceptor hri);
}
