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
 */

package io.monkey.context;

import io.micronaut.context.ApplicationContext;
import org.junit.Test;

import javax.inject.Singleton;

import static org.junit.Assert.assertEquals;

/**
 * @author Michael
 * Created at: 2019/2/17 16:59
 */
public class MonkeyApplicationContextTests {

    @Test
    public void testMonkeyApplicationContext() {

        try(ApplicationContext context = MonkeyApplicationContext.run("test")) {
            assertEquals("greeting", context.getBean(Greeting.class).greet());
        }

    }

    @Singleton
    static class Greeting {

        public String greet() {
            return "greeting";
        }
    }

}
