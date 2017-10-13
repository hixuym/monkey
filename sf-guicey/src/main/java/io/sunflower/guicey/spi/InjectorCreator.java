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

package io.sunflower.guicey.spi;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import io.sunflower.guicey.InjectorBuilder;
import io.sunflower.guicey.SimpleInjectorCreator;

/**
 * Contract that makes Guice getInjector creation a pluggable strategy and allows for typed
 * extensions to the Injector within the context of the strategy.  An InjectorCreator
 * may also implement post getInjector creation operations such as callings
 * prior to returning form createInjector().
 * 
 * InjectorCreator can be used directly with a module,
 * 
 * <code>
   new LifecycleInjectorCreator().createInjector(new MyApplicationModule());
 * </code>
 * 
 * Alternatively, InjectorCreator can be used in conjunction with the {@link InjectorBuilder} DSL
 * 
 * <code>
  LifecycleInjector getInjector = InjectorBuilder
      .fromModule(new MyApplicationModule())
      .overrideWith(new MyApplicationOverrideModule())
      .combineWith(new AdditionalModule()
      .createInjector(new LifecycleInjectorCreator());
  }
 * </code>
 * 
 * See {@link SimpleInjectorCreator}
 */
public interface InjectorCreator<I extends Injector> {
    I createInjector(Stage stage, Module module);
}
