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

import java.util.concurrent.ExecutorService;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.util.concurrent.MoreExecutors;
import io.ebean.EbeanServer;
import io.ebean.SqlQuery;
import io.sunflower.db.TimeBoundHealthCheck;
import io.sunflower.util.Duration;

public class EbeanServerHealthCheck extends HealthCheck {

  private final EbeanServer ebeanServer;
  private final String validationQuery;
  private final TimeBoundHealthCheck timeBoundHealthCheck;

  public EbeanServerHealthCheck(EbeanServer ebeanServer,
      String validationQuery) {
    this(MoreExecutors.newDirectExecutorService(), Duration.seconds(0), ebeanServer,
        validationQuery);
  }

  public EbeanServerHealthCheck(ExecutorService executorService,
      Duration duration,
      EbeanServer ebeanServer,
      String validationQuery) {
    this.ebeanServer = ebeanServer;
    this.validationQuery = validationQuery;
    this.timeBoundHealthCheck = new TimeBoundHealthCheck(executorService, duration);
  }

  @Override
  protected Result check() throws Exception {
    return timeBoundHealthCheck.check(() -> {

      ebeanServer.beginTransaction();
      try {

        SqlQuery sqlQuery = ebeanServer.createSqlQuery(validationQuery);

        sqlQuery.findList();

        ebeanServer.commitTransaction();
      } catch (Exception e) {
        ebeanServer.rollbackTransaction();

        throw e;
      } finally {
        ebeanServer.endTransaction();
      }

      return Result.healthy();
    });
  }
}
