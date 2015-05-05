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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Checkout example.
 */
final class Checkout {

    /**
     * Empty constructor.
     */
    private Checkout() {

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

        // NOTE: Only a placeholder session object. Actual session object
        // might be a javax.servlet.http.HttpSession for JSP for example.
        Map<String, Object> session = new HashMap<String, Object>();

        // Merchant ID
        final String eid = "0";
        final String secret = "sharedSecret";

        IConnector connector = Connector.create(
                secret, IConnector.TEST_BASE_URL);

        final Map<String, Object> cart = new HashMap<String, Object>() {
            {
                put("items", new ArrayList<HashMap<String, Object>>() {
                    {
                        add(new HashMap<String, Object>() {
                            {
                                put("quantity", 2);
                                put("reference", "123456789");
                                put("name", "Klarna t-shirt");
                                put("unit_price", 12300);
                                put("discount_rate", 1000);
                                put("tax_rate", 2500);
                            }
                        });
                        add(new HashMap<String, Object>() {
                            {
                                put("quantity", 1);
                                put("type", "shipping_fee");
                                put("reference", "SHIPPING");
                                put("name", "Shipping Fee");
                                put("unit_price", 4900);
                                put("tax_rate", 2500);
                            }
                        });
                    }
                });
            }
        };

        try {
            Order order = null;

            URI resourceURI = null;
            // Retrieve location from session object.
            if (session.containsKey("klarna_checkout")) {
                resourceURI = new URI((String) session.get("klarna_checkout"));
            }

            if (resourceURI != null) {
                try {
                    order = new Order(connector, resourceURI);
                    order.fetch();

                    // Reset cart
                    Map<String, Object> data = new HashMap<String, Object>() {
                        {
                            put("cart", cart);
                        }
                    };

                    order.update(data);
                } catch (Exception e) {
                    // Reset session
                    order = null;
                    session.remove("klarna_checkout");
                }
            }

            if (order == null) {
                // Start a new session.
                final Map<String, Object> merchant = new HashMap<String, Object>() {
                    {
                        put("id", eid);
                        put("terms_uri", "http://example.com/terms.html");
                        put("checkout_uri", "http://example.com/checkout.jsp");
                        put("confirmation_uri",
                                "http://example.com/thank-you.jsp"
                                        + "?sid=123&klarna_order={checkout.order.uri}");
                        // You can not receive push notification on a
                        // non-publicly available uri.
                        put("push_uri",
                                "http://example.com/push.jsp"
                                        + "?sid=123&klarna_order={checkout.order.uri}");
                    }
                };

                // Add gui settings
                final Map<String, Object> gui = new HashMap<String, Object>() {
                    {
                        put("layout", "desktop");
                    } // or mobile
                };

                Map<String, Object> data = new HashMap<String, Object>() {
                    {
                        put("purchase_country", "SE");
                        put("purchase_currency", "SEK");
                        put("locale", "sv-se");
                        put("merchant", merchant);
                        put("cart", cart);
                        put("gui", gui);
                    }
                };

                order = new Order(connector);
                order.create(data);
                order.fetch();
            }

            // Store checkout session.
            // JSP: session.setAttribute(
            //         "klarna_checkout", order.getLocation());
            session.put("klarna_checkout", order.getLocation());

            // Display checkout
            Map<String, Object> gui = (Map<String, Object>) order.get("gui");
            String snippet = (String) gui.get("snippet");

            // Output the snippet on your page.
            System.out.println(String.format("<div>%s</div>", snippet));
            // DESKTOP: Width of containing block shall be at least 750px
            // MOBILE: Width of containing block shall be 100% of browser
            // window (No padding or margin)
        } catch (ErrorResponseException e) {
            JSONObject json = (JSONObject) e.getJson();

            System.out.println(json.get("http_status_message"));
            System.out.println(json.get("internal_message"));
        }
    }
}
