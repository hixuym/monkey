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

package io.monkey.resteasy.jsr310;

import io.monkey.resteasy.params.AbstractParam;

import java.time.ZoneId;

/**
 * A parameter encapsulating time-zone IDs, such as Europe/Paris.
 * All non-parsable values will return a {@code 400 Bad Request} response.
 *
 * @author michael
 * @see ZoneId
 */
public class ZoneIdParam extends AbstractParam<ZoneId> {
    public ZoneIdParam(final String input) {
        super(input);
    }

    @Override
    protected ZoneId parse(final String input) {
        return ZoneId.of(input);
    }
}
