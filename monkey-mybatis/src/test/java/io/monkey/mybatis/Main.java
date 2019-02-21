/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.monkey.mybatis;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.ComputePlatform;
import io.micronaut.context.env.Environment;

import static io.micronaut.context.env.Environment.CLOUD_PLATFORM_PROPERTY;

/**
 * @author Michael
 * Created at: 2019/2/21 14:09
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty(CLOUD_PLATFORM_PROPERTY, ComputePlatform.OTHER.name());

        long start = System.currentTimeMillis();
        try(ApplicationContext context = ApplicationContext.run("test")) {
            Environment environment = context.getBean(Environment.class);
            System.out.println(environment.getActiveNames());
        }

        long end = System.currentTimeMillis();

        System.out.println("startup time: " + (end - start) + " ms.");
    }

}
