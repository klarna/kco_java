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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.klarna.checkout.*;

/**
 * Checkout example.
 */
public class Checkout extends BaseExample {

    /**
     * Run the example file. Called from the menu.
     *
     * @param session Session storage.
     *
     * @return session
     */
    @Override
    Map<String, Object> run(Map<String, Object> session) {
        this.session = session;
        if (this.session.containsKey("klarna_checkout")) {
            this.fetch();
        } else {
            this.create();
        }
        return this.session;
    }

    /**
     * Set up Example.
     *
     * @return Connector to use.
     */
    public Connector setUpExample() {
        try {
            Order.baseUri = new URI(
                    "https://klarnacheckout.apiary.io/checkout/orders");
        } catch (URISyntaxException ex) {
            System.err.println("Malformed URI");
            System.exit(0);
        }

        try {
            return new Connector(new Digest("sharedSecret"));
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("SHA-256 digest not supported.");
            System.exit(0);
        }
        return null;
    }

    /**
     * Create a Checkout instance.
     */
    public void create() {
        Connector connector = setUpExample();

        final Map<String, Object> product = new HashMap<String, Object>() {{
           put("type", "physical") ;
           put("reference", "BANANA01");
           put("name", "Banana");
           put("unit_price", 450);
           put("discount_rate", 0);
           put("tax_rate", 2500);
        }};

        final Map<String, Object> shipping = new HashMap<String, Object>() {{
           put("type", "shipping_fee") ;
           put("reference", "SHIPPING");
           put("name", "Shipping Fee");
           put("unit_price", 450);
           put("discount_rate", 0);
           put("tax_rate", 2500);
        }};

        Order order = new Order();

        order.parse(new HashMap<String, Object>() {{
            put("purchase_country", "SE");
            put("purchase_currency", "SEK");
            put("locale", "sv-se");
            put("merchant", new HashMap<String, Object>(){{
                put("id", 2);
                put("terms_uri", "http://localhost/terms_and_agreements");
                put("checkout_uri", "http://localhost/checkout");
                put("confirmation_uri", "http://localhost/thank_you");
                put("push_uri", "http://localhost/push");
            }});
            put("cart", new HashMap<String, Object>(){{
                put("total_price_including_tax", 9000);
                put("items", new ArrayList<Map<String, Object>>() {{
                    add(product);
                    add(shipping);
                }});
            }});
        }});
        try {
            order.create(connector);
            order.fetch(connector);
        } catch (IOException ex) {
            Logger.getLogger(
                    Checkout.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.session.put("klarna_checkout", order.getLocation());

        finalizeExample(order);
    }

    /**
     * Fetch a checkout instance.
     */
    public void fetch() {
        Order order = new Order();
        Connector connector = setUpExample();

        try {
            order.fetch(connector, (URI) session.get("klarna_checkout"));
        } catch (IOException ex) {
            Logger.getLogger(
                    Checkout.class.getName()).log(Level.SEVERE, null, ex);
        }

        finalizeExample(order);
    }

    /**
     * Show client snippet.
     *
     * @param order Checkout Resource
     */
    public void finalizeExample(Order order) {
        String snippet = (String) ((HashMap)order.get("gui")).get("snippet");

        System.out.println("<div>");
        System.out.println(snippet);
        System.out.println("</div>");
    }

}
