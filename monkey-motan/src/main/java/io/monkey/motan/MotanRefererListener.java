/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.motan;

import com.google.common.base.Strings;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.exception.MotanFrameworkException;
import io.monkey.motan.annotation.MotanReferer;

import java.lang.reflect.Field;

/**
 * @author Michael
 * Created at: 2019/1/4 15:39
 */
public class MotanRefererListener implements TypeListener {

    private final MotanFactory motanFactory;

    public MotanRefererListener(MotanFactory motanFactory) {
        this.motanFactory = motanFactory;
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<?> clazz = type.getRawType();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(MotanReferer.class)) {
                    encounter.register(new MotanRefererMemberInjector<>(field, motanFactory));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private static class MotanRefererMemberInjector<T> implements MembersInjector<T> {

        private final Field field;
        private final MotanFactory motanFactory;
        private final MotanReferer motanReferer;

        public MotanRefererMemberInjector(Field field, MotanFactory motanFactory) {
            this.field = field;
            this.field.setAccessible(true);
            this.motanReferer = field.getAnnotation(MotanReferer.class);
            this.motanFactory = motanFactory;
        }

        @Override
        public void injectMembers(T instance) {

            String id = motanReferer.id();

            if (Strings.isNullOrEmpty(id)) {
                id = field.getType().getName();
            }

            RefererConfig refererConfig = motanFactory.getReferersConfig().get(id);

            if (refererConfig == null) {
                refererConfig = new RefererConfig();
                refererConfig.setCheck("false");
            }

            if (!Strings.isNullOrEmpty(motanReferer.directUrl())
                && Strings.isNullOrEmpty(refererConfig.getDirectUrl())) {
                refererConfig.setDirectUrl(motanReferer.directUrl());
            }

            refererConfig.setInterface(field.getType());

            motanFactory.registerReferer(id, refererConfig);

            try {
                field.set(instance, refererConfig.getRef());
            } catch (IllegalAccessException e) {
                throw new MotanFrameworkException(e);
            }

        }
    }
}
