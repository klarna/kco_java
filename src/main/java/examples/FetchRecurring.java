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
import com.klarna.checkout.RecurringStatus;
import org.json.simple.JSONObject;

import java.io.IOException;
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
        final String recurringToken = "ABC-123";

        IConnector connector = Connector.create(
                secret, IConnector.TEST_BASE_URL);

        RecurringStatus recurringStatus = new RecurringStatus(
                connector, recurringToken);

        try {
            recurringStatus.fetch();

            System.out.println(recurringStatus.get("payment_method"));
        } catch (ErrorResponseException e) {
            JSONObject json = e.getJson();

            System.out.println(json.get("http_status_message"));
            System.out.println(json.get("internal_message"));
        }
    }
}
