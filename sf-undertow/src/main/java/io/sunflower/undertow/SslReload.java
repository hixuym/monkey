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

package io.sunflower.undertow;

import java.util.function.Consumer;

import io.sunflower.sec.ssl.SslContextFactory;

/**
 * Provides the ability to modify an existing ssl factory with new configuration options.
 */
public class SslReload {

  private final SslContextFactory factory;
  private final Consumer<SslContextFactory> configurer;

  public SslReload(SslContextFactory factory, Consumer<SslContextFactory> configurer) {
    this.factory = factory;
    this.configurer = configurer;
  }

  public void reload() throws Exception {
    getFactory().reload(configurer);
  }

  public void reload(SslContextFactory factory) throws Exception {
    factory.reload(configurer);
  }

  public SslContextFactory getFactory() {
    return factory;
  }
}
