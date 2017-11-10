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

/**
 * A parameter encapsulating long values. All non-decimal values will return a {@code 400 Bad
 * Request} response.
 * @author michael
 */
public class LongParam extends AbstractParam<Long> {
    public LongParam(String input) {
        super(input);
    }

    public LongParam(String input, String parameterName) {
        super(input, parameterName);
    }

    @Override
    protected String errorMessage(Exception e) {
        return "%s is not a number.";
    }

    @Override
    protected Long parse(String input) {
        return Long.valueOf(input);
    }
}
