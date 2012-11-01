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
 * File containing the HttpClientWrapper tests.
 */
package com.klarna.checkout;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for HttpClientWrapper.
 */
public class HttpClientWrapperTest {

    /**
     * Test to ascertain the inheritance of HttpClientWrapper.
     */
    @Test
    public void testInheritance() {
        HttpClientWrapper hcw = new HttpClientWrapper(mock(HttpParams.class));
        assertThat(hcw, instanceOf(IHttpClient.class));
        assertThat(hcw, instanceOf(DefaultHttpClient.class));

        hcw = new HttpClientWrapper(
                mock(ClientConnectionManager.class), mock(HttpParams.class));

        assertThat(hcw, instanceOf(IHttpClient.class));
        assertThat(hcw, instanceOf(DefaultHttpClient.class));
    }
}
