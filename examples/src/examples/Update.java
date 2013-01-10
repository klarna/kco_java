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
 * File containing the Update example.
 */
// [[examples-update]]
package examples;

import com.klarna.checkout.Connector;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.Order;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The update checkout example.
 */
public final class Update {

    /**
     * The example.
     */
    public void example()
            throws URISyntaxException, NoSuchAlgorithmException, IOException {

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

        // Merchant ID
        final String eid = "0";
        final String secret = "sharedSecret";

        Order.setContentType(
                "application/vnd.klarna.checkout.aggregated-order-v2+json");
        URI uri = new URI(
                "https://checkout.testdrive.klarna.com/checkout/orders");

        Order.setBaseUri(uri);
        IConnector connector = Connector.create(secret);

        URI resourceURI = new URI(
                "https://checkout.testdrive.klarna.com/checkout/orders/ABC123");
        Order order = new Order(connector, resourceURI);

        // Reset cart
        Map<String, Object> data = new HashMap<String, Object>() {
            {
                put("cart", cart);
            }
        };

        order.update(data);
    }
}
// [[examples-update]]