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
 * File containing the Checkout example.
 */
package examples;

import com.klarna.checkout.Connector;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.Order;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checkout example.
 */
public final class Checkout {

    /**
     * The example.
     */
    public void example() {
        // NOTE: Only a placeholder session object. Actual session object might
        // be a javax.servlet.http.HttpSession for JSP for example.
        Map<String, Object> session = new HashMap<String, Object>();

        try {
            final int eid = 2;
            final String secret = "sharedSecret";

            URI uri = new URI(
                    "https://klarnacheckout.apiary.io/checkout/orders");

            Order.setBaseUri(uri);
            Order.setContentType(
                    "application/vnd.klarna.checkout.aggregated-order-v2+json");
            Order order = new Order();

            // Retrieve location from session object.
            URI resourceURI = null;
            IConnector connector = Connector.create(secret);
            if (session.containsKey("klarna_checkout")) {
                //JSP: session.getAttribute("klarna_checkout");
                resourceURI = (URI) session.get("klarna_checkout");
            }

            if (resourceURI == null) {
                // Start a new session.
                order.set("purchase_country", "SE");
                order.set("purchase_currency", "SEK");
                order.set("locale", "sv-se");

                Map<String, Object> merchant = new HashMap<String, Object>() {
                    {
                        put("id", eid);
                        put("terms_uri", "http://localhost/terms,html");
                        put("checkout_uri", "http://localhost/checkout");
                        put("confirmation_uri", "http://localhost/thank-you");
                        // You can not recieve push notification on a
                        // non-publicly available uri.
                        put("push_uri", "http://localhost/push");
                    }
                };
                order.set("merchant", merchant);

                final List<HashMap<String, Object>> cartItems;
                cartItems = new ArrayList<HashMap<String, Object>>() {
                    {
                        add(new HashMap<String, Object>() {
                            {
                                put("type", "physical");
                                put("reference", "BANAN01");
                                put("name", "Banana");
                                put("unit_price", 450);
                                put("discount_rate", 0);
                                put("tax_rate", 2500);
                            }
                        });
                        add(new HashMap<String, Object>() {
                            {
                                put("type", "shipping_fee");
                                put("reference", "SHIPPING");
                                put("name", "Shipping Fee");
                                put("unit_price", 450);
                                put("discount_rate", 0);
                                put("tax_rate", 2500);
                            }
                        });
                    }
                };
                order.set("cart", new HashMap<String, Object>() {
                    {
                        put("items", cartItems);
                    }
                });

                order.create(connector);

                order.fetch(connector);
            } else {
                // Resume session.
                order.fetch(connector, resourceURI);
            }

            //JSP: session.setAttribute("klarna_checkout", order.getLocation());
            session.put("klarna_checkout", order.getLocation());

            Map<String, Object> gui;
            gui = (HashMap<String, Object>) order.get("gui");

            String snippet = (String) gui.get("snippet");

            // Output the snippet on your page.
            System.out.println(String.format("<div>%s</div>", snippet));

        } catch (Exception ex) {
            // Handle exception.
            ex.printStackTrace();
        }
    }
}
