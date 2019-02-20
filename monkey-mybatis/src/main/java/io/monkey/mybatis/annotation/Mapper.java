/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.monkey.mybatis.annotation;

import io.micronaut.aop.Introduction;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Executable;
import io.micronaut.context.annotation.Type;
import io.monkey.mybatis.interceptor.MapperIntroductionAdvice;

import javax.inject.Singleton;
import java.lang.annotation.*;

/**
 * @author Michael
 * Created at: 2019/2/19 16:47
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Introduction
@Type(MapperIntroductionAdvice.class)
@Bean
@Singleton
@Executable(processOnStartup = true)
public @interface Mapper {
    /**
     * the dataSource
     * @return the datasource repo for
     */
    String value() default "default";
}
