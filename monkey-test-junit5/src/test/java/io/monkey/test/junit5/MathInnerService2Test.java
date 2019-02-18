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

package io.monkey.test.junit5;

import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@MonkeyTest
class MathInnerService2Test {

    @Inject
    MathService mathService;

    @Inject
    MathService[] services;

    /**
     * Tests that it is possible to have 2 mock beans
     */
    @Test
    void testInnerMockAgain() {
        final int result = mathService.compute(10);

        Assertions.assertEquals(
                1,
                services.length
        );
        Assertions.assertEquals(
                50,
                result
        );
        Assertions.assertTrue(mathService instanceof MyService);
    }

    @MockBean(MathServiceImpl.class)
    static class MyService implements MathService {

        @Override
        public Integer compute(Integer num) {
            return 50;
        }
    }
}

