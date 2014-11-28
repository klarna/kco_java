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
 * File containing the UserAgent class
 */
package com.klarna.checkout;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserAgent.
 */
public class UserAgent {

    /**
     * UserAgentField.
     */
    protected static class Field {

        /**
         * Key of the field.
         */
        private String key;
        /**
         * Name of the field.
         */
        private String name;
        /**
         * Version of the field.
         */
        private String version;
        /**
         * Optional values.
         */
        private String[] options;

        /**
         * Constructor.
         *
         * @param fieldKey Key of the field.
         * @param fieldName Name of the field.
         * @param fieldVersion Version of the field.
         */
        public Field(
                final String fieldKey,
                final String fieldName,
                final String fieldVersion) {
            this.key = fieldKey;
            this.name = fieldName;
            this.version = fieldVersion;
            this.options = new String[0];
        }

        /**
         * Constructor with optional values.
         *
         * @param fieldKey Key of the field.
         * @param fieldName Name of the field.
         * @param fieldVersion Version of the field.
         * @param fieldOptions Options for the field.
         */
        public Field(
                final String fieldKey,
                final String fieldName,
                final String fieldVersion,
                final String... fieldOptions) {
            this.key = fieldKey;
            this.name = fieldName;
            this.version = fieldVersion;
            this.options = fieldOptions;
        }

        /**
         * Get the string representation of UserAgentField.
         *
         * @return String
         */
        @Override
        public final String toString() {
            String result = String.format(
                    "%s/%s_%s", this.key, this.name, this.version);

            if (this.options.length == 0) {
                return result;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("(");
            builder.append(this.options[0]);

            for (int i = 1; i < this.options.length; i++) {
                builder.append(" ; ");
                builder.append(this.options[i]);
            }

            builder.append(")");

            return result.concat(" ").concat(builder.toString());
        }
    }
    /**
     * Fields associated to the UserAgent.
     */
    private final List<UserAgent.Field> fields;

    /**
     * Constructor.
     */
    public UserAgent() {
        this.fields = new ArrayList<UserAgent.Field>();
        try {
            this.addField(
                    new UserAgent.Field(
                        "Library", "Klarna.ApiWrapper", "1.1.3"));
            this.addField(
                    new UserAgent.Field(
                        "OS",
                        System.getProperty("os.name"),
                        System.getProperty("os.version")));
            this.addField(
                    new UserAgent.Field(
                        "Language",
                        "Java",
                        System.getProperty("java.version"),
                        "Vendor/" + System.getProperty("java.vendor")
                                .replace(" ", "-"),
                        "VM/" + System.getProperty("java.vm.name")
                                .replace(" ", "-")));
        } catch (KlarnaException ex) {
            Logger.getLogger(
                    UserAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Add a field to the UserAgent.
     *
     * @param field The UserAgentField to add.
     *
     * @throws KlarnaException If the key already exists.
     */
    public final void addField(final UserAgent.Field field)
            throws KlarnaException {
        for (UserAgent.Field object : this.fields) {
            if (object.key.compareTo(field.key) == 0) {
                throw new KlarnaException(
                        "Unable to redefine field " + field.key);
            }
        }
        this.fields.add(field);
    }

    /**
     * Get the string representation of UserAgent.
     *
     * @return String
     */
    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.fields.remove(0));

        for (UserAgent.Field field : this.fields) {
            builder.append(" ");
            builder.append(field);
        }

        return builder.toString();
    }
}
