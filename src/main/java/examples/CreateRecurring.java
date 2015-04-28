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
 * File containing the CreateRecurring example.
 */
package examples;

import com.klarna.checkout.Connector;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.RecurringOrder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The create recurring order example.
 */
final class CreateRecurring {

    /**
     * Empty constructor.
     */
    private CreateRecurring() {

    }

    /**
     * Runs the example code.
     * <p/>
     * Note! First you must have created a regular aggregated order with the
     * option "recurring" set to true. After that order has received either
     * status "checkout_complete" or "created" you can fetch that resource
     * and retrieve the "recurring_token" property which is needed to create
     * recurring orders.
     *
     * @param args Command line arguments
     * @throws URISyntaxException       If URIs are incorrect
     * @throws NoSuchAlgorithmException If connector couldn't be created
     * @throws IOException              If api call failed
     */
    public static void main(final String[] args)
            throws URISyntaxException, NoSuchAlgorithmException, IOException {

        // Merchant ID
        final String eid = "0";
        final String secret = "sharedSecret";
        final String recurringToken = "ABC-123";

        IConnector connector = Connector.create(
                secret, IConnector.TEST_BASE_URL);

        RecurringOrder recurringOrder = new RecurringOrder(
                connector, recurringToken);

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

        final Map<String, String> address = new HashMap<String, String>() {
            {
                put("postal_code", "12345");
                put("email", "checkout-se@testdrive.klarna.com");
                put("country", "se");
                put("city", "Ankeborg");
                put("family_name", "Approved");
                put("given_name", "Testperson-se");
                put("street_address", "Stårgatan 1");
                put("phone", "070 111 11 11");
            }
        };

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

        // If the order should be activated automatically.
        // Set to true if you instead want a invoice created
        // otherwise you will get a reservation.
        final Boolean activate = false;

        Map<String, Object> data = new HashMap<String, Object>() {
            {
                put("purchase_country", "SE");
                put("purchase_currency", "SEK");
                put("locale", "sv-se");
                put("merchant", merchant);
                put("billing_address", address);
                put("shipping_address", address);
                put("cart", cart);
                put("activate", activate);
            }
        };

        recurringOrder.create(data);

        System.out.println(
                recurringOrder.get(activate ? "invoice" : "reservation"));
    }
}
