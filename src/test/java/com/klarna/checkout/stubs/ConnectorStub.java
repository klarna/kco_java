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
 * File containing the stub implementation of the Connector class
 */

package com.klarna.checkout.stubs;

import com.klarna.checkout.ConnectorOptions;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.IResource;
import java.net.URI;
import java.util.HashMap;

/**
 * Stub implementation of the IConnector interface.
 */
public class ConnectorStub implements IConnector {

    /**
     * Location.
     */
    private URI location;

    /**
     * Holder for the data sent to apply.
     */
    private HashMap<String, Object> applied;

    /**
     * Get an applied argument.
     *
     * @param key method, resource or options
     *
     * @return the applied object.
     */
    public Object getApplied(String key) {
        return this.applied.get(key);
    }

    /**
     * Stub of the apply method.
     *
     * @param method HTTP Method
     * @param resource IResource implementation
     * @param options ConnectorOptions object.
     */
    @Override
    public void apply(
            String method, IResource resource, ConnectorOptions options) {
        this.applied = new HashMap<String, Object>();
        this.applied.put("method", method);
        this.applied.put("resource", resource);
        this.applied.put("options", options);

        if (this.location != null) {
            resource.setLocation(this.location);
        }
    }

    /**
     * Stub of the apply method.
     *
     * @param method HTTP Method
     * @param resource IResource implementation
     */
    public void apply(String method, IResource resource) {
        apply(method, resource, new ConnectorOptions());
    }

    /**
     * Set a new URI location.
     *
     * @param newLocation the new location of the resource.
     */
    public void setLocation(URI newLocation) {
        this.location = newLocation;
    }

}
