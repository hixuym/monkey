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

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

public class Injectors {

  public static <T> Set<T> instanceOf(Injector injector, Class<T> baseClass) {

    Set<T> resutls = Sets.newHashSet();

    for (Map.Entry<Key<?>, Binding<?>> binding : injector.getBindings().entrySet()) {
      if (baseClass.isAssignableFrom(binding.getKey().getTypeLiteral().getRawType())) {
        Provider<? extends T> provider = (Provider) binding.getValue().getProvider();
        resutls.add(provider.get());
      }
    }

    return resutls;
  }

  public static <K, V> Map<K, V> mapOf(Injector injector, TypeLiteral<Map<K, V>> typeLiteral) {
    return injector.getInstance(Key.get(typeLiteral));
  }

  public static <T> Set<T> setOf(Injector injector, TypeLiteral<Set<T>> typeLiteral) {
    return injector.getInstance(Key.get(typeLiteral));
  }

}
