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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.sunflower.client.HttpClientConfiguration;
import io.sunflower.configuration.ConfigurationParsingException;
import io.sunflower.configuration.ConfigurationValidationException;
import io.sunflower.configuration.DefaultConfigurationFactoryFactory;
import io.sunflower.jackson.Jackson;
import io.sunflower.validation.BaseValidator;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class HttpClientConfigurationTest {

    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private HttpClientConfiguration configuration;

    private void load(String configLocation) throws Exception {
        configuration = new DefaultConfigurationFactoryFactory<HttpClientConfiguration>()
                .create(HttpClientConfiguration.class,
                BaseValidator.newValidator(),
                objectMapper, "sf")
                .build(new File(Resources.getResource(configLocation).toURI()));
    }

    @Test
    public void testNoProxy() throws Exception {
        load("./yaml/no_proxy.yml");
        Assertions.assertThat(configuration.getProxyConfiguration()).isNull();
    }

    @Test
    public void testFullConfigBasicProxy() throws Exception {
        load("yaml/proxy.yml");

        ProxyConfiguration proxy = configuration.getProxyConfiguration();
        assertThat(proxy).isNotNull();

        assertThat(proxy.getHost()).isEqualTo("192.168.52.11");
        assertThat(proxy.getPort()).isEqualTo(8080);
        assertThat(proxy.getScheme()).isEqualTo("https");

        AuthConfiguration auth = proxy.getAuth();
        assertThat(auth).isNotNull();
        assertThat(auth.getUsername()).isEqualTo("secret");
        assertThat(auth.getPassword()).isEqualTo("stuff");

        List<String> nonProxyHosts = proxy.getNonProxyHosts();
        assertThat(nonProxyHosts).contains("localhost", "192.168.52.*", "*.quickstarters.com");
    }

    @Test
    public void testFullConfigNtlmProxy() throws Exception {
        load("yaml/proxy_ntlm.yml");

        ProxyConfiguration proxy = configuration.getProxyConfiguration();
        assertThat(proxy).isNotNull();

        assertThat(proxy.getHost()).isEqualTo("192.168.52.11");
        assertThat(proxy.getPort()).isEqualTo(8080);
        assertThat(proxy.getScheme()).isEqualTo("https");

        AuthConfiguration auth = proxy.getAuth();
        assertThat(auth).isNotNull();
        assertThat(auth.getUsername()).isEqualTo("secret");
        assertThat(auth.getPassword()).isEqualTo("stuff");
        assertThat(auth.getAuthScheme()).isEqualTo("NTLM");
        assertThat(auth.getRealm()).isEqualTo("realm");
        assertThat(auth.getHostname()).isEqualTo("workstation");
        assertThat(auth.getDomain()).isEqualTo("HYPERCOMPUGLOBALMEGANET");
        assertThat(auth.getCredentialType()).isEqualTo("NT");

        List<String> nonProxyHosts = proxy.getNonProxyHosts();
        assertThat(nonProxyHosts).contains("localhost", "192.168.52.*", "*.quickstarters.com");
    }

    @Test
    public void testNoScheme() throws Exception {
        load("./yaml/no_scheme.yml");

        ProxyConfiguration proxy = configuration.getProxyConfiguration();
        assertThat(proxy).isNotNull();
        assertThat(proxy.getHost()).isEqualTo("192.168.52.11");
        assertThat(proxy.getPort()).isEqualTo(8080);
        assertThat(proxy.getScheme()).isEqualTo("http");
    }

    @Test
    public void testNoAuth() throws Exception {
        load("./yaml/no_auth.yml");

        ProxyConfiguration proxy = configuration.getProxyConfiguration();
        assertThat(proxy).isNotNull();
        assertThat(proxy.getHost()).isNotNull();
        assertThat(proxy.getAuth()).isNull();
    }

    @Test
    public void testNoPort() throws Exception {
        load("./yaml/no_port.yml");

        ProxyConfiguration proxy = configuration.getProxyConfiguration();
        assertThat(proxy).isNotNull();
        assertThat(proxy.getHost()).isNotNull();
        assertThat(proxy.getPort()).isEqualTo(-1);
    }

    @Test
    public void testNoNonProxy() throws Exception {
        load("./yaml/no_port.yml");

        ProxyConfiguration proxy = configuration.getProxyConfiguration();
        assertThat(proxy.getNonProxyHosts()).isNull();
    }

    @Test(expected = ConfigurationValidationException.class)
    public void testNoHost() throws Exception {
        load("yaml/bad_host.yml");
    }

    @Test(expected = ConfigurationValidationException.class)
    public void testBadPort() throws Exception {
        load("./yaml/bad_port.yml");
    }

    @Test(expected = ConfigurationParsingException.class)
    public void testBadScheme() throws Exception {
        load("./yaml/bad_scheme.yml");
    }

    @Test(expected = ConfigurationValidationException.class)
    public void testBadAuthUsername() throws Exception {
        load("./yaml/bad_auth_username.yml");
    }

    @Test(expected = ConfigurationValidationException.class)
    public void testBadPassword() throws Exception {
        load("./yaml/bad_auth_password.yml");
    }

    @Test(expected = ConfigurationValidationException.class)
    public void testBadAuthScheme() throws Exception {
        load("./yaml/bad_auth_scheme.yml");
    }

    @Test(expected = ConfigurationValidationException.class)
    public void testBadCredentialType() throws Exception {
        load("./yaml/bad_auth_credential_type.yml");
    }
}
