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

package io.monkey.jaxrs.jsr310;

import io.monkey.jaxrs.params.AbstractParam;

import java.time.YearMonth;

/**
 * A parameter encapsulating year and month values. All non-parsable values will return a {@code 400 Bad
 * Request} response.
 *
 * @author michael
 * @see YearMonth
 */
public class YearMonthParam extends AbstractParam<YearMonth> {
    public YearMonthParam(final String input) {
        super(input);
    }

    @Override
    protected YearMonth parse(final String input) {
        return YearMonth.parse(input);
    }
}
