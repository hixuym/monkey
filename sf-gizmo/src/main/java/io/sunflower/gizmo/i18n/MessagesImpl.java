/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.sunflower.gizmo.i18n;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.Result;

@Singleton
public class MessagesImpl implements Messages {

    private static Logger logger = LoggerFactory.getLogger(MessagesImpl.class);

    private final GizmoConfiguration configuration;
    private final Lang lang;

    @Inject
    public MessagesImpl(GizmoConfiguration configuration,
                        Lang lang) {
        this.configuration = configuration;
        this.lang = lang;
    }


    @Override
    public Optional<String> get(String key,
                                Context context,
                                Optional<Result> result,
                                Object... parameter) {
        Optional<String> language = lang.getLanguage(context, result);
        return get(key, language, parameter);
    }

    @Override
    public Optional<String> get(String key, Optional<String> language, Object... params) {

        return Optional.empty();
    }

    @Override
    public Map<Object, Object> getAll(Context context, Optional<Result> result) {

        Optional<String> language = lang.getLanguage(context, result);
        return getAll(language);

    }

    @Override
    public Map<Object, Object> getAll(Optional<String> language) {

        return Maps.newHashMap();

    }

    @Override
    public String getWithDefault(String key,
                                 String defaultMessage,
                                 Context context,
                                 Optional<Result> result,
                                 Object... params) {

        Optional<String> language = lang.getLanguage(context, result);

        return getWithDefault(key, defaultMessage, language, params);

    }

    @Override
    public String getWithDefault(String key,
                                 String defaultMessage,
                                 Optional<String> language,
                                 Object... params) {
        Optional<String> value = get(key, language, params);
        if (value.isPresent()) {
            return value.get();
        } else {
            MessageFormat messageFormat = getMessageFormatForLocale(defaultMessage, language);
            return messageFormat.format(params);
        }
    }

    MessageFormat getMessageFormatForLocale(String value, Optional<String> language) {
        Locale locale = lang.getLocaleFromStringOrDefault(language);
        MessageFormat messageFormat = new MessageFormat(value, locale);
        return messageFormat;
    }
}
