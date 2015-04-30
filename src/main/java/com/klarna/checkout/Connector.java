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
 * File containing the Connector Factory.
 */
package com.klarna.checkout;

import java.security.NoSuchAlgorithmException;

/**
 * Connector Factory.
 */
public final class Connector {

    /**
     * Hide constructor for utility class.
     */
    private Connector() {
    }

    /**
     * Create a Connector to use.
     *
     * @param sharedSecret String used to sign communications with Klarna
     * @return a IConnector implementation
     * @throws NoSuchAlgorithmException if the JVM does not support SHA-256
     */
    public static IConnector create(final String sharedSecret)
            throws NoSuchAlgorithmException {
        return new BasicConnector(new Digest(sharedSecret));
    }

    /**
     * Create a Connector to use with a specific base URL.
     *
     * @param sharedSecret String used to sign communications with Klarna
     * @param baseURL      Base URL to use
     * @return a IConnector implementation
     * @throws NoSuchAlgorithmException if the JVM does not support SHA-256
     */
    public static IConnector create(
            final String sharedSecret, final String baseURL)
            throws NoSuchAlgorithmException {
        return new BasicConnector(new Digest(sharedSecret)) {
            {
                setBaseUri(baseURL);
            }
        };
    }
}
