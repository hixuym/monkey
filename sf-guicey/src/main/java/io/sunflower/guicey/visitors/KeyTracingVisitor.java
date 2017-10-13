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

package io.sunflower.guicey.visitors;

import com.google.inject.Binding;
import com.google.inject.spi.DefaultElementVisitor;
import io.sunflower.guicey.InjectorBuilder;

/**
 * Visitor for logging only the Key for each {@code Element} binding
 *
 * To use with {@link InjectorBuilder}
 *
 * <code> InjectorBuilder .fromModule(new MyApplicationModule) .forEachElement(new
 * BindingTracingVisitor()) .createInjector(); </code>
 */
public class KeyTracingVisitor extends DefaultElementVisitor<String> {

  @Override
  public <T> String visit(Binding<T> binding) {
    return binding.getKey().toString();
  }
}
