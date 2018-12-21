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

package io.sunflower.inject.event;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented

/**
 * Marks a method as an {@link ApplicationEvent} subscriber.
 *
 * Subscriber methods should be public, void, and accept only one argument implementing {@link ApplicationEvent}
 *
 * <code>
 * public class MyService {
 *
 *    {@literal @}EventListener
 *    public void onEvent(MyCustomEvent event) {
 *
 *    }
 *
 * }
 * </code>
 *
 */
public @interface EventListener {

}
