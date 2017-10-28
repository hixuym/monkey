/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.client.proxy;

import com.google.common.collect.ImmutableList;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NonProxyListProxyRoutePlannerTest {

    private HttpHost proxy = new HttpHost("192.168.52.15");
    private NonProxyListProxyRoutePlanner routePlanner = new NonProxyListProxyRoutePlanner(proxy,
            ImmutableList.of("localhost", "*.example.com", "192.168.52.*"));
    private HttpRequest httpRequest = mock(HttpRequest.class);
    private HttpContext httpContext = mock(HttpContext.class);

    @Test
    public void testProxyListIsNotSet() {
        assertThat(new NonProxyListProxyRoutePlanner(proxy, null).getNonProxyHostPatterns()).isEmpty();
    }

    @Test
    public void testHostNotInBlackList() throws Exception {
        assertThat(routePlanner.determineProxy(new HttpHost("dropwizard.io"), httpRequest, httpContext))
                .isEqualTo(proxy);
    }

    @Test
    public void testPlainHostIsMatched() throws Exception {
        assertThat(routePlanner.determineProxy(new HttpHost("localhost"), httpRequest, httpContext))
                .isNull();
    }

    @Test
    public void testHostWithStartWildcardIsMatched() throws Exception {
        assertThat(
                routePlanner.determineProxy(new HttpHost("test.example.com"), httpRequest, httpContext))
                .isNull();
    }

    @Test
    public void testHostWithEndWildcardIsMatched() throws Exception {
        assertThat(routePlanner.determineProxy(new HttpHost("192.168.52.94"), httpRequest, httpContext))
                .isNull();
    }
}
