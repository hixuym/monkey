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

import io.sunflower.util.Duration;

/**
 * A parameter encapsulating duration values. All non-parsable values will return a {@code 400 Bad
 * Request} response. Supports all input formats the {@link Duration} class supports.
 * @author michael
 */
public class DurationParam extends AbstractParam<Duration> {

    public DurationParam(String input) {
        super(input);
    }

    public DurationParam(String input, String parameterName) {
        super(input, parameterName);
    }

    @Override
    protected String errorMessage(Exception e) {
        return "%s is not a valid duration.";
    }

    @Override
    protected Duration parse(String input) throws Exception {
        return Duration.parse(input);
    }

}
