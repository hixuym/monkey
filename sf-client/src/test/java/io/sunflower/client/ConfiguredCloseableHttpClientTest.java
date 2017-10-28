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

package io.sunflower.client;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ConfiguredCloseableHttpClientTest {

    public ConfiguredCloseableHttpClient configuredClient;
    @Mock
    private CloseableHttpClient closeableHttpClientMock;
    @Mock
    private RequestConfig defaultRequestConfigMock;

    @Before
    public void setUp() {
        configuredClient = new ConfiguredCloseableHttpClient(closeableHttpClientMock,
                defaultRequestConfigMock);
    }

    @Test
    public void getDefaultRequestConfig_returns_config_provided_at_construction() {
        assertThat(configuredClient.getDefaultRequestConfig()).isEqualTo(defaultRequestConfigMock);
    }

    @Test
    public void getClient_returns_config_provided_at_construction() {
        assertThat(configuredClient.getClient()).isEqualTo(closeableHttpClientMock);
    }
}
