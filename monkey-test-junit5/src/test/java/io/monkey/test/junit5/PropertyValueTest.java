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

import io.micronaut.context.annotation.Property;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MonkeyTest
@Property(name = "foo.bar", value = "stuff")
class PropertyValueTest {

    @Property(name = "foo.bar")
    String val;

    @Test
    void testInitialValue() {
        assertEquals("stuff", val);
    }

    @Property(name = "foo.bar", value = "changed")
    @Test
    void testValueChanged() {
        assertEquals("changed", val);
    }

    @Test
    void testValueRestored() {
        assertEquals("stuff", val);
    }
}
