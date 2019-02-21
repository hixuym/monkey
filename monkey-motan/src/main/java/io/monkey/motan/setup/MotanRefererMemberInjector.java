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

import com.google.common.base.Strings;
import com.google.inject.MembersInjector;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.exception.MotanFrameworkException;
import io.monkey.motan.MotanFactory;
import io.monkey.motan.annotation.MotanReferer;
import io.monkey.setup.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Michael
 * Created at: 2019/1/4 22:07
 */
class MotanRefererMemberInjector implements MembersInjector {

    private Field field;
    private Method method;
    private final MotanFactory motanFactory;
    private final Environment environment;
    private final MotanReferer motanReferer;
    private final boolean fieldInject;

    MotanRefererMemberInjector(Method method, MotanFactory motanFactory, Environment environment) {
        this.fieldInject = false;
        this.method = method;
        this.motanFactory = motanFactory;
        this.motanReferer = method.getAnnotation(MotanReferer.class);
        this.environment = environment;
    }

    MotanRefererMemberInjector(Field field, MotanFactory motanFactory, Environment environment) {
        this.field = field;
        this.field.setAccessible(true);
        this.fieldInject = true;
        this.motanFactory = motanFactory;
        this.motanReferer = field.getAnnotation(MotanReferer.class);
        this.environment = environment;
    }

    @Override
    public void injectMembers(Object instance) {

        Class clazz;

        if (fieldInject) {
            clazz = field.getType();
        } else {
            clazz = method.getParameterTypes()[0];
        }

        String id = motanReferer.id();

        if (Strings.isNullOrEmpty(id)) {
            id = clazz.getName();
        }

        RefererConfig refererConfig = motanFactory.getReferersConfig().get(id);

        if (refererConfig == null) {
            refererConfig = new RefererConfig();
            refererConfig.setCheck("false");
        }

        refererConfig.setApplication(environment.getName());

        if (!Strings.isNullOrEmpty(motanReferer.directUrl())
            && Strings.isNullOrEmpty(refererConfig.getDirectUrl())) {
            refererConfig.setDirectUrl(motanReferer.directUrl());
        }

        if (void.class.equals(motanReferer.interfaceClass())
            && clazz.isInterface()) {
            refererConfig.setInterface(clazz);
        } else if (!void.class.equals(motanReferer.interfaceClass())) {
            refererConfig.setInterface(motanReferer.interfaceClass());
        }

        motanFactory.registerReferer(id, refererConfig);

        try {

            if (fieldInject) {
                field.set(instance, refererConfig.getRef());
            } else {
                method.invoke(instance, refererConfig.getRef());
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MotanFrameworkException(e);
        }

    }


}
