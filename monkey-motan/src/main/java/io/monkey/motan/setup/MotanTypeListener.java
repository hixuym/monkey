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

package io.monkey.motan.setup;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import io.monkey.motan.MotanFactory;
import io.monkey.motan.annotation.MotanReferer;
import io.monkey.motan.annotation.MotanService;
import io.monkey.setup.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Michael
 * Created at: 2019/1/4 21:46
 */
class MotanTypeListener implements TypeListener {

    private final MotanFactory motanFactory;
    private final Environment environment;

    public MotanTypeListener(MotanFactory motanFactory, Environment environment) {
        this.motanFactory = motanFactory;
        this.environment = environment;
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<?> clazz = type.getRawType();
        while (clazz != null) {

            if (clazz.isAnnotationPresent(MotanService.class)) {
                encounter.register(new MotanServiceExportListener(clazz, motanFactory, environment));
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(MotanReferer.class)) {
                    encounter.register(new MotanRefererMemberInjector(field, motanFactory, environment));
                }
            }

            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(MotanReferer.class)) {
                    continue;
                }
                String name = method.getName();
                if (name.length() > 3 && name.startsWith("set")
                    && method.getParameterTypes().length == 1
                    && Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())) {

                    encounter.register(new MotanRefererMemberInjector(method, motanFactory, environment));

                }
            }

            clazz = clazz.getSuperclass();

        }
    }
}
