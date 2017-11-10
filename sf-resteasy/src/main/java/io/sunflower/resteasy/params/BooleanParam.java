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
 * A parameter encapsulating boolean values. If the query parameter value is {@code "true"},
 * regardless of case, the returned value is {@link Boolean#TRUE}. If the query parameter value is
 * {@code "false"}, regardless of case, the returned value is {@link Boolean#FALSE}. All other
 * values will return a {@code 400 Bad Request} response.
 * @author michael
 */
public class BooleanParam extends AbstractParam<Boolean> {
    public BooleanParam(String input) {
        super(input);
    }

    public BooleanParam(String input, String parameterName) {
        super(input, parameterName);
    }

    @Override
    protected String errorMessage(Exception e) {
        return "%s must be \"true\" or \"false\".";
    }

    @Override
    protected Boolean parse(String input) throws Exception {
        if ("true".equalsIgnoreCase(input)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(input)) {
            return Boolean.FALSE;
        }
        throw new Exception();
    }
}
