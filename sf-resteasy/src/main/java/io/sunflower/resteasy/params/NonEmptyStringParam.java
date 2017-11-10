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

import com.google.common.base.Strings;

import java.util.Optional;

/**
 * A parameter encapsulating optional string values with the condition that empty string inputs are
 * interpreted as being absent. This class is useful when it is desired for empty parameters to be
 * synonymous with absent parameters instead of empty parameters evaluating to
 * {@code Optional.of("")}.
 * @author michael
 */
public class NonEmptyStringParam extends AbstractParam<Optional<String>> {
    public NonEmptyStringParam(String input) {
        super(input);
    }

    public NonEmptyStringParam(String input, String parameterName) {
        super(input, parameterName);
    }

    @Override
    protected Optional<String> parse(String input) throws Exception {
        return Optional.ofNullable(Strings.emptyToNull(input));
    }
}
