/*
 * Copyright 2018-2023 Monkey, Inc
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

package io.monkey.ebean.setup;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import io.monkey.ebean.Txn;

import java.lang.reflect.Method;

import static com.google.inject.matcher.Matchers.*;

/**
 * @author Michael
 * Created at: 2019/1/15 17:39
 */
class EbeanModule extends AbstractModule {

    private static final AbstractMatcher<Method> DECLARED_BY_OBJECT = new AbstractMatcher<Method>() {
        @Override
        public boolean matches(Method method) {
            return method.getDeclaringClass() == Object.class;
        }
    };

    private static final AbstractMatcher<Method> SYNTHETIC = new AbstractMatcher<Method>() {
        @Override
        public boolean matches(Method method) {
            return method.isSynthetic();
        }
    };

    private final ServerConfig serverConfig;

    EbeanModule(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    protected void configure() {

        EbeanServerProvider provider = new EbeanServerProvider(serverConfig);

        if (serverConfig.isDefaultServer()) {
            bind(EbeanServer.class).toProvider(provider);
        } else {
            bind(EbeanServer.class).annotatedWith(Names.named(serverConfig.getName())).toProvider(provider);
        }

        // class-level @Txn
        LocalTxnInterceptor txnInterceptor = new LocalTxnInterceptor();

        bindInterceptor(any(),
            not(SYNTHETIC).and(not(DECLARED_BY_OBJECT)).and(Matchers.annotatedWith(Txn.class)), txnInterceptor);
        // Intercept classes annotated with Transactional, but avoid "double"
        // interception when a mathod is also annotated inside an annotated
        // class.
        bindInterceptor(annotatedWith(Txn.class),
            not(SYNTHETIC).and(not(DECLARED_BY_OBJECT)).and(not(annotatedWith(Txn.class))), txnInterceptor);
    }
}
