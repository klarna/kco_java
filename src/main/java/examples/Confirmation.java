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
import java.util.Map;

/**
 * Thank you example.
 */
final class Confirmation {

    /**
     * Empty constructor.
     */
    private Confirmation() {

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
            throws URISyntaxException, NoSuchAlgorithmException, IOException {

        // Shared secret.
        final String secret = "sharedSecret";

        IConnector connector = Connector.create(
                secret, IConnector.TEST_BASE_URL);

        // This is just a placeholder for the example.
        // For example in jsp you could do
        //      request.getParameter("checkout_uri");
        URI checkoutId = new URI(
                "https://checkout.testdrive.klarna.com/checkout/orders/123");

        Order order = new Order(connector, checkoutId);

        try {
            order.fetch();

            final String status = (String) order.get("status");
            if (!status.equals("checkout_complete")) {
                // Report error
                System.out.println("Checkout not completed, redirect");
            }

            Map<String, Object> gui = (Map<String, Object>) order.get("gui");

            String snippet = gui.get("snippet").toString();

            // Output the snippet to the customer.
            System.out.println(String.format("<div>%s</div>", snippet));
            // Clear session object from klarna_checkout data.
            // session.removeAttribute("klarna_checkout");
        } catch (ErrorResponseException e) {
            JSONObject json = e.getJson();

            System.out.println(json.get("http_status_message"));
            System.out.println(json.get("internal_message"));
        }
    }
}
