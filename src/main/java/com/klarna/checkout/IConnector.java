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
 * File containing the IConnector interface
 */
package com.klarna.checkout;

/**
 * Interface for the Connector object.
 */
public interface IConnector {

    /**
     * Applying the method on the specific resource.
     *
     * @param method HTTP method
     * @param resource Resource implementation
     * @param options Connector Options
     */
    void apply(String method, IResource resource, ConnectorOptions options);
}
