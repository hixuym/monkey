/*
 *
 *  *  Copyright 2018-2023 Monkey, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package io.monkey.undertow;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.monkey.setup.Environment;
import io.monkey.ssl.SslContextFactoryFactory;
import io.undertow.Undertow;

@JsonTypeName("https")
public class HttpsServerFactory extends HttpServerFactory {

    @JsonProperty("ssl")
    private SslContextFactoryFactory sslContextFactoryFactory = new SslContextFactoryFactory();

    @Override
    protected Undertow.ListenerBuilder getListener(Environment environment) {

        // workaround for chrome issue w/ JVM and self-signed certs triggering
        // an IOException that can safely be ignored
        ch.qos.logback.classic.Logger root
                = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("io.undertow.request.io");
        root.setLevel(Level.WARN);

        Undertow.ListenerBuilder builder = super.getListener(environment);
        builder.setType(Undertow.ListenerType.HTTPS);
        builder.setSslContext(sslContextFactoryFactory.build(environment));

        return builder;
    }

    public SslContextFactoryFactory getSslContextFactoryFactory() {
        return sslContextFactoryFactory;
    }

    public void setSslContextFactoryFactory(SslContextFactoryFactory sslContextFactoryFactory) {
        this.sslContextFactoryFactory = sslContextFactoryFactory;
    }

}
