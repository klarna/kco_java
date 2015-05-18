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

package com.klarna.checkout.stubs;

import com.klarna.checkout.ConnectorOptions;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.IResource;
import org.apache.http.HttpResponse;

import java.io.IOException;
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
     * Host.
     */
    private String baseUri = IConnector.TEST_BASE_URL;

    /**
     * Holder for the data sent to apply.
     */
    private HashMap<String, Object> applied;

    /**
     * Get base uri.
     *
     * @return Base uri
     */
    public String getBaseUri() {
        return baseUri;
    }

    /**
     * Set base uri.
     *
     * @param base Base uri
     */
    public void setBaseUri(final String base) {
        this.baseUri = base;
    }

    /**
     * Get an applied argument.
     *
     * @param key method, resource or options
     * @return the applied object.
     */
    public Object getApplied(final String key) {
        return this.applied.get(key);
    }

    /**
     * Stub of the apply method.
     *
     * @param method   HTTP Method
     * @param resource IResource implementation
     * @param options  ConnectorOptions object.
     * @return HttpResponse object
     * @throws IOException in case of an I/O error
     */
    public HttpResponse apply(
            final String method,
            final IResource resource,
            final ConnectorOptions options) throws IOException {
        this.applied = new HashMap<String, Object>();
        this.applied.put("method", method);
        this.applied.put("resource", resource);
        this.applied.put("options", options);

        if (this.location != null) {
            resource.setLocation(this.location);
        }
        return null;
    }

    /**
     * Stub of the apply method.
     *
     * @param method   HTTP Method
     * @param resource IResource implementation
     * @return HttpResponse object
     * @throws IOException in case of an I/O error
     */
    public HttpResponse apply(
            final String method, final IResource resource) throws IOException {
        return apply(method, resource, new ConnectorOptions());
    }

    /**
     * Set a new URI location.
     *
     * @param newLocation the new location of the resource.
     */
    public void setLocation(final URI newLocation) {
        this.location = newLocation;
    }

    /**
     * Specify a socket timeout to use.
     *
     * @param milliseconds Milliseconds to use as timeout.
     */
    public void setTimeout(final int milliseconds) {
        return;
    }

}
