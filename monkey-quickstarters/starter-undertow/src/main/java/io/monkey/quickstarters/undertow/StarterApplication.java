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

package io.monkey.quickstarters.undertow;

import io.monkey.Application;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
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
        return "undertow";
    }

    @Override
    public void initialize(Bootstrap<StarterConfiguration> bootstrap) {
    }

    @Override
    public void run(StarterConfiguration configuration, Environment environment) {
    }

}
