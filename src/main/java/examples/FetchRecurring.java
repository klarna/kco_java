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
 * File containing the FetchRecurring example.
 */
package examples;

import com.klarna.checkout.Connector;
import com.klarna.checkout.IConnector;
import com.klarna.checkout.RecurringStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

/**
 * The fetch recurring order status example.
 */
final class FetchRecurring {

    /**
     * Empty constructor.
     */
    private FetchRecurring() {

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

        final String secret = "sharedSecret";

        URI uri = new URI(
                "https://checkout.testdrive.klarna.com/checkout/recurring/ABC-123");

        IConnector connector = Connector.create(secret);

        RecurringStatus recurringStatus = new RecurringStatus(
                connector, uri);

        recurringStatus.fetch();

        System.out.println(recurringStatus.get("payment_method"));
    }
}
