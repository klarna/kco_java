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
 * File containing the Fetch example.
 */
// [[examples-fetch]]
package examples;

import com.klarna.checkout.Connector;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.Order;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

/**
 * The fetch checkout example.
 */
public final class Fetch {

    /**
     * The example.
     */
    public void example()
            throws URISyntaxException, NoSuchAlgorithmException, IOException {

        final String secret = "sharedSecret";

        Order.setContentType(
                "application/vnd.klarna.checkout.aggregated-order-v2+json");

        URI resourceURI = new URI(
                "https://checkout.testdrive.klarna.com/checkout/orders/ABC123");

        IConnector connector = Connector.create(secret);

        Order order = new Order(connector, resourceURI);
        order.fetch();
    }
}
// [[examples-fetch]]