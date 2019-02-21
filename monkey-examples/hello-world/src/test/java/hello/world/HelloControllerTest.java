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

package hello.world;

import io.monkey.test.extensions.junit5.MonkeyTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Michael
 * Created at: 2019/2/17 21:13
 */
@MonkeyTest
public class HelloControllerTest {

    @Inject
    HelloClient helloClient;

    @Test
    public void testHello() {
        assertEquals(
            "Hello Fred!",
            helloClient.hello("Fred").blockingGet());
    }

}
