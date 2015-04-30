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

import java.net.URI;
import java.util.Map;

/**
 * Resource Interface.
 */
public interface IResource {

    /**
     * Get the location.
     *
     * @return the URL of the resource.
     */
    URI getLocation();

    /**
     * Set the URL of the resource.
     *
     * @param uri URI object pointing to the resource
     */
    void setLocation(URI uri);

    /**
     * Set the content type header.
     *
     * @param type Content type of the resource
     */
    void setContentType(String type);

    /**
     * Get the content type header.
     *
     * @return The content type of the resource.
     */
    String getContentType();

    /**
     * Set the accept header.
     *
     * @param accept The accept type of the resource.
     */
    void setAccept(String accept);

    /**
     * Get the accept header.
     *
     * @return The accept type of the resource.
     */
    String getAccept();

    /**
     * Update resource with new data.
     *
     * @param data new data to update the resource with
     */
    void parse(Map<String, Object> data);

    /**
     * Get the basic representation of the resource.
     *
     * @return Basic representation of the resource.
     */
    Map marshal();
}
