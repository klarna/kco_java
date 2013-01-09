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
 * File containing the Handler class.
 */
package com.klarna.checkout;

import java.io.IOException;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * ResponseHandler implementation that does not throw exception
 * on status codes 3xx.
 */
class Handler implements ResponseHandler<HttpResponse> {

    /**
     * Resource object.
     */
    private final IResource resource;

    /**
     * Constructor.
     *
     * @param res IResource implementation.
     */
    Handler(final IResource res) {
        this.resource = res;
    }

    /**
     * Handle response and don't throw exception for 3xx codes.
     *
     * @param response Response from HTTP Request
     *
     * @return HttpResponse object
     *
     * @throws IOException in case of a problem or the connection was aborted
     */
    public HttpResponse handleResponse(final HttpResponse response)
            throws IOException {

        this.verifyStatusCode(response);

        String payload = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            parsePayload(payload);
        }

        return response;
    }

    /**
     * Verify Status Code.
     *
     * @param result HTTP Response object
     *
     * @throws HttpResponseException if code is between 400 and 599 (inclusive)
     * @throws IOException if response could not be read
     */
    protected void verifyStatusCode(final HttpResponse result)
            throws HttpResponseException, IOException {
        if (result.getStatusLine().getStatusCode() >= 400
                && result.getStatusLine().getStatusCode() <= 599) {
            throw new HttpResponseException(
                    result.getStatusLine().getStatusCode(),
                    EntityUtils.toString(result.getEntity()));
        }
    }

    /**
     * Parse the payload.
     *
     * @param payload JSON payload to parse.
     *
     * @throws IOException if parse was unsuccessful.
     */
    protected void parsePayload(final String payload) throws IOException {
        try {
            JSONParser jsonParser = new JSONParser();
            Object json = jsonParser.parse(payload);
            resource.parse((HashMap<String, Object>) json);
        } catch (ParseException ex) {
            // Interface dictates strict exception types.
            throw new IOException(ex);
        }
    }
}
