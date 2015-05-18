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

package com.klarna.checkout;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;

/**
 * UserAgentTest.
 */
public class UserAgentTest {

    /**
     * UserAgent instance.
     */
    private UserAgent agent;

    /**
     * Set up for the tests.
     */
    @Before
    public void setUp() {
        this.agent = new UserAgent();
    }

    /**
     * Test that the default keys exist in the string.
     */
    @Test
    public void testToString() {
        final String string = this.agent.toString();
        assertTrue(
                "Library key wasn't found",
                string.matches(".*Library\\/[^\\ ]+_[^\\ ]+.*"));
        assertTrue(
                "OS key wasn't found",
                string.matches(".*OS\\/[^\\ ]+_[^\\ ]+.*"));
        assertTrue(
                "Language key wasn't found",
                string.matches(".*Language\\/[^\\ ]+_[^\\ ]+.*"));
        assertTrue(
                "Vendor key wasn't found",
                string.matches(".*Vendor\\/.*"));
        assertTrue(
                "VM key wasn't found",
                string.matches(".*VM\\/.*"));
    }

    /**
     * Test that an added field exist in the string.
     */
    @Test
    public void testAddField() {
        String[] arr = new String[]{"butter/3", "cheese/0.1"};
        this.agent.addField(
                new UserAgent.Field(
                        "Bread", "Crumb", "2.5", arr));
        final String string = this.agent.toString();
        assertTrue(
                "New field wasn't found",
                string.matches(
                        ".*Bread/Crumb_2\\.5.* \\(butter\\/3 ; cheese\\/0.1\\).*"));
    }

    /**
     * Test that trying to add an already existing field logs a message.
     */
    @Test
    public void testAddFieldKeyAlreadyExists() {
        Logger logger = Logger.getLogger(UserAgent.class.getName());
        OutputStream logCapturingStream = new ByteArrayOutputStream();
        StreamHandler customLogHandler = new StreamHandler(
                logCapturingStream,
                logger.getParent().getHandlers()[0].getFormatter());

        logger.addHandler(customLogHandler);

        this.agent.addField(
                new UserAgent.Field("Language", "Something", "9"));

        customLogHandler.flush();

        assertThat(
                logCapturingStream.toString(),
                containsString("SEVERE: Unable to redefine field Language"));
    }

    /**
     * Test that the entire User-Agent string looks like it should.
     */
    @Test
    public void testCompleteFormat() {
        final String string = this.agent.toString();

        final String format = "^(Library\\/Klarna\\.ApiWrapper_[^,\\s]+) (OS\\/[^,\\s]+) (Language\\/[^,\\s]+) \\((Vendor\\/[^;]+) ; (VM\\/[^;]+)\\)$";

        assertTrue(
                "Format is incorrect",
                string.matches(format));
    }
}
