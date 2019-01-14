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

package io.monkey.quickstarters.resteasy;

import io.monkey.Application;
import io.monkey.datasource.PooledDataSourceFactory;
import io.monkey.ebean.EbeanBundle;
import io.monkey.quickstarters.resteasy.auth.BasicAuthModule;
import io.monkey.quickstarters.resteasy.resources.HelloworldResource;
import io.monkey.resteasy.setup.ResteasyBundle;
import io.monkey.resteasy.setup.ResteasyFactory;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
import io.monkey.setup.GuicifyEnvironment;
//import org.conscrypt.OpenSSLProvider;
//
//import java.security.Security;

/**
 * @author michael created on 17/9/2
 */
public class StarterApplication extends Application<StarterConfiguration> {

//    static {
//        Security.addProvider(new OpenSSLProvider());
//
//    }

    public static void main(String[] args) throws Exception {
//        String text = BaseEncoding.base64().encode("admin:123456".getBytes(Charsets.UTF_8));
//        System.out.println(text);
        new StarterApplication().run(args);
    }

    @Override
    public String getName() {
        return "resteasy";
    }

    @Override
    public void initialize(Bootstrap<StarterConfiguration> bootstrap) {
        bootstrap.addBundle(new EbeanBundle<StarterConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(StarterConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new ResteasyBundle<StarterConfiguration>() {

            @Override
            public ResteasyFactory getResteasyFactory(StarterConfiguration configuration) {
                return configuration.getResteasyFactory();
            }

            @Override
            protected void bindResources(GuicifyEnvironment facotry) {
                facotry.register(HelloworldResource.class);
            }
        });
    }

    @Override
    public void run(StarterConfiguration configuration, Environment environment) {
        environment.guicify().register(new BasicAuthModule());
    }

}
