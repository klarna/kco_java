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
 * File containing the UserAgent class
 */
package com.klarna.checkout;

import java.util.ArrayList;
import java.util.List;

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
         * @param key Key of the field.
         * @param name Name of the field.
         * @param version Version of the field.
         */
        public Field(String key, String name, String version) {
            this.key = key;
            this.name = name;
            this.version = version;
            this.options = new String[0];
        }

        /**
         * Constructor with optional values.
         *
         * @param key Key of the field.
         * @param name Name of the field.
         * @param version Version of the field.
         * @param options Extra values.
         */
        public Field(String key, String name, String version, String... options) {
            this.key = key;
            this.name = name;
            this.version = version;
            this.options = options;
        }

        /**
         * Get the string representation of UserAgentField.
         *
         * @return String
         */
        @Override
        public final String toString() {
            String result = this.key + "/" + this.name + "_" + this.version;
            String part = "";
            if (this.options.length > 0) {
                part = part.format(
                        "(%s)", UserAgent.implode(this.options, " ; "));
                result = result.concat(" ").concat(part);
            }
            return result;
        }
    }

    /**
     * Fields associated to the UserAgent.
     */
    private final List<UserAgent.Field> fields;

    /**
     * Constructor.
     *
     * @throws KlarnaException If keys already exists in fields.
     */
    public UserAgent() throws KlarnaException {
        this.fields = new ArrayList<UserAgent.Field>();
        this.addField(
                new UserAgent.Field("Library", "Klarna.ApiWrapper", "1.0"));
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
                    new String[]{
                        ("Vendor/" + System.getProperty("java.vendor")),
                        ("VM/" + System.getProperty("java.vm.name"))}));
    }

    /**
    * Implode a String array with a glue.
    *
    * @param options The strings to implode.
    * @param glue The glue to use.
    *
    * @return String The imploded string.
    */
   protected static String implode(String[] options, String glue) {
       final StringBuilder stringBuilder = new StringBuilder();
       stringBuilder.append(options[0]);
       for (int i = 1; i < options.length; i++) {
           stringBuilder.append(glue);
           stringBuilder.append(options[i]);
       }
       return stringBuilder.toString();
   }

    /**
     * Add a field to the UserAgent.
     *
     * @param field The UserAgentField to add.
     *
     * @throws KlarnaException If the key already exists.
     */
    public final void addField(UserAgent.Field field)
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
        String[] fields = new String[]{this.fields.toString()};
        return UserAgent.implode(fields, " ");
    }

}
