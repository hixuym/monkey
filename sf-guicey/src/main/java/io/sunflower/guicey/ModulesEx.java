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

package io.sunflower.guicey;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public final class ModulesEx {

  private static final Module EMPTY_MODULE = new AbstractModule() {
    @Override
    protected void configure() {
    }
  };

  public static Module emptyModule() {
    return EMPTY_MODULE;
  }

  public static Module fromEagerSingleton(final Class<?> type) {
    return new AbstractModule() {
      @Override
      protected void configure() {
        bind(type).asEagerSingleton();
      }
    };
  }

  public static <T> Module fromInstance(final T object) {
    return new AbstractModule() {
      @Override
      protected void configure() {
        bind((Class<T>) object.getClass()).toInstance(object);
        this.requestInjection(object);
      }
    };
  }
}
