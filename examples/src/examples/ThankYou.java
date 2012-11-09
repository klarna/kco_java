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
 * File containing the thank you example.
 */
package examples;

import com.klarna.checkout.Connector;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.Order;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Thank you example.
 */
class ThankYou {

    /**
     * The example.
     */
    public void example() {

        try {
            // Shared secret.
            final String secret = "sharedSecret";

            Order order = new Order();
            IConnector connector = Connector.create(secret);

            // This is just a placeholder for the example.
            // For example in jsp you could do
            //      request.getParameter("checkout_uri");
            URI checkoutId = new URI(
                    "https://klarnacheckout.apiary.io/checkout/orders/12");

            order.fetch(connector, checkoutId);

            if (!((String) order.get("status")).equals("checkout_complete")) {
                // Report error
                // System.out.println("Checkout not completed, redirect");
            }

            Map<String, Object> gui;
            gui = (HashMap<String, Object>) order.get("gui");

            String snippet = gui.get("snippet").toString();

            // Output the snippet to the customer.
            System.out.println(String.format("<div>%s</div>", snippet));
            // Clear session object from klarna_checkout data.
            // session.removeAttribute("klarna_checkout");

        } catch (Exception ex) {
            // Handle exception.
            ex.printStackTrace();
        }
    }
}
