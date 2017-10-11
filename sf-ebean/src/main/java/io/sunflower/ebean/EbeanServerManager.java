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

package io.sunflower.ebean;

import io.ebeaninternal.server.lib.ShutdownManager;
import io.sunflower.db.ManagedDataSource;
import io.sunflower.lifecycle.Managed;

public class EbeanServerManager implements Managed {

  private final ManagedDataSource dataSource;

  public EbeanServerManager(ManagedDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void start() throws Exception {
    this.dataSource.start();
  }

  @Override
  public void stop() throws Exception {
    this.dataSource.stop();
    ShutdownManager.shutdown();
  }
}
