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

package io.sunflower.undertow.handler;

import com.codahale.metrics.jvm.ThreadDump;
import com.google.common.collect.ImmutableMultimap;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;

public class ThreadDumpTask extends Task {

  private transient ThreadDump threadDump;

  public ThreadDumpTask() {
    super("dump-thread");
    try {
      // Some PaaS like Google App Engine blacklist java.lang.managament
      this.threadDump = new ThreadDump(ManagementFactory.getThreadMXBean());
    } catch (NoClassDefFoundError ncdfe) {
      this.threadDump = null; // we won't be able to provide thread dump
    }
  }

  @Override
  public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output)
      throws Exception {
    if (threadDump == null) {
      output.println("Sorry your runtime environment does not allow to dump threads.");
      return;
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    threadDump.dump(out);

    output.println(out.toString());
  }
}
