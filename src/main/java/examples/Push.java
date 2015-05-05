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

package examples;

import com.klarna.checkout.Connector;
import com.klarna.checkout.ErrorResponseException;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.Order;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Push Example.
 */
final class Push {

    /**
     * Empty constructor.
     */
    private Push() {

    }

    /**
     * Runs the example code.
     *
     * @param args Command line arguments
     * @throws URISyntaxException       If URIs are incorrect
     * @throws NoSuchAlgorithmException If connector couldn't be created
     * @throws IOException              If api call failed
     */
    public static void main(final String[] args)
            throws NoSuchAlgorithmException, URISyntaxException, IOException {

        // Shared secret.
        final String secret = "sharedSecret";

        IConnector connector = Connector.create(
                secret, IConnector.TEST_BASE_URL);

        // This is just a placeholder for the example.
        // For example in jsp you could do
        //      request.getParameter("checkout_uri");
        URI resourceURI = new URI(
                "https://checkout.testdrive.klarna.com/checkout/orders/123");

        Order order = new Order(connector, resourceURI);

        try {
            order.fetch();
        } catch (ErrorResponseException e) {
            JSONObject json = (JSONObject) e.getJson();

            System.out.println(json.get("http_status_message"));
            System.out.println(json.get("internal_message"));
        }

        if ((order.get("status")).equals("checkout_complete")) {
            final Map<String, Object> reference =
                    new HashMap<String, Object>() {
                        {
                            put("orderid1", UUID.randomUUID().toString());
                        }
                    };

            Map<String, Object> updateData;
            updateData = new HashMap<String, Object>() {
                {
                    put("status", "created");
                    put("merchant_reference", reference);
                }
            };

            try {
                order.update(updateData);
            } catch (ErrorResponseException e) {
                JSONObject json = (JSONObject) e.getJson();

                System.out.println(json.get("http_status_message"));
                System.out.println(json.get("internal_message"));
            }
        }
    }
}
