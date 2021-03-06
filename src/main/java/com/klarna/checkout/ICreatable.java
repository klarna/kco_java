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

import java.io.IOException;
import java.util.Map;

/**
 * Interface for resources that makes them creatable.
 */
public interface ICreatable {

    /**
     * Create a new resource with the given fields.
     *
     * @param createData Data to create with
     * @throws IOException in case of an I/O error
     */
    void create(final Map<String, Object> createData) throws IOException;
}
