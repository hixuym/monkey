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

package io.sunflower.guice.event;

/***
 * Interface for receiving to events of a given type. Can be registered explicitly
 * via {@link ApplicationEventDispatcher#registerListener(ApplicationEventListener)}
 * or implicitly in Guice by detecting by all bindings for instances of {@link ApplicationEvent}
 * */
public interface ApplicationEventListener<T extends ApplicationEvent> {

    void onEvent(T event);

}
