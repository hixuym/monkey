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

package io.sunflower.guice.advise;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@literal @}ProvidesWithAdvice is a Guice usage pattern whereby a method of a module can be
 * annotated with {@literal @}ProvidesWithAdvice to return the initial version of an object and then
 * have {@literal @}Advises annotated methods that can modify the object.  All of this happens BEFORE
 * the final object is injected.  This functionality is useful for frameworks that wish to provide
 * a default implementation of a type but allow extensions and customizations by installing modules
 * with {@literal @}Advises annotated methods.
 * <p>
 * For example,
 * <p>
 * <pre>
 * {@literal @}ProvidesWithAdvice
 * List<String> provideList() { return new ArrayList<>(Arrays.asList("a", "b", "c")); }
 *
 * {@literal @}Advises
 * UnaryOperator<List<String>> addMoreToList() { return list -> list.add("d"); }
 * </pre>
 * <p>
 * will add "d" to the original ["a", "b", "c"].  When List&ltString&gt is finally injected it'll have
 * ["a", "b", "c", "d"].
 * <p>
 * Note that {@literal @}ProvideWithAdvice can be used with qualifiers such as {@literal @}Named
 * with matching qualifiers added to the {@literal @}Advises methods.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface ProvidesWithAdvice {

}
