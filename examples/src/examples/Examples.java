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
 * File containing the Examples class.
 */
package examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Example runnable file.
 */
public class Examples {

    /**
     * Session storage.
     */
    private static Map<String, Object> session = new HashMap<String, Object>();

    /**
     * Menu options.
     */
    private static SortedMap<String, String> options = new TreeMap<String, String>() {{
        put("1", "Create Checkout");
        put("2", "Push");
        put("3", "Thankyou");
        put("q", "Quit");
        }};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        while (true) {
            BaseExample example = menu();
            session = example.run(session);
        }
    }

    /**
     * Menu handling.
     *
     * @return Example process to run.
     */
    private static BaseExample menu() {
        printMenu();
        Scanner scan = new Scanner(System.in);
        String s = scan.next();

        if (!options.containsKey(s)) {
            return menu();
        }

        if (s.equals("q")) {
            System.out.println("Goodbye!");
            System.exit(1);
        }

        if (s.equals("1")) {
            return new Checkout();
        }

        if (s.equals("2")) {
            return new Push();
        }

        if (s.equals("3")) {
            return new ThankYou();
        }

        return menu();

    }

    /**
     * Print the menu.
     */
    private static void printMenu() {
        System.out.println("===== Checkout Examples =====");
        for (Map.Entry<String, String> entry : options.entrySet()) {
            System.out.println(entry.getKey() + ") " + entry.getValue());
        }
        System.out.println("> ");
    }
}
