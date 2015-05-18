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

import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.json.simple.JSONObject;

/**
 * Exception for API error responses.
 */
public class ErrorResponseException extends HttpResponseException {

    /**
     * JSON object.
     */
    private JSONObject json;

    /**
     * Constructor.
     *
     * @param statusLine  HTTP statusLine entity.
     * @param jsonPayload JSON payload.
     */
    public ErrorResponseException(
            final StatusLine statusLine, final JSONObject jsonPayload) {
        super(
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase());

        json = jsonPayload;
    }

    /**
     * Get the JSON representation of the content.
     *
     * @return JSON object.
     */
    public JSONObject getJson() {
        return json;
    }
}
