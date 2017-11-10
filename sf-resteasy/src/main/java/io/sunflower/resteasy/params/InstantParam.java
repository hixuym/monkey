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

package io.sunflower.resteasy.params;

import java.time.Instant;

/**
 * A parameter encapsulating date/time values. All non-parsable values will return a {@code 400 Bad Request} response.
 * @author michael
 */
public class InstantParam extends AbstractParam<Instant> {
    public InstantParam(final String input) {
        super(input);
    }

    public InstantParam(final String input, final String parameterName) {
        super(input, parameterName);
    }

    @Override
    protected String errorMessage(Exception e) {
        return "%s must be in a ISO-8601 format.";
    }

    @Override
    protected Instant parse(final String input) throws Exception {
        return Instant.parse(input);
    }
}
