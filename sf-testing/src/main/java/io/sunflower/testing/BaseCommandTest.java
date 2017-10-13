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

package io.sunflower.testing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Optional;

import io.sunflower.cli.Cli;
import io.sunflower.setup.Bootstrap;
import io.sunflower.util.JarLocation;
import org.junit.After;
import org.junit.Before;

public abstract class BaseCommandTest {

  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private final InputStream originalIn = System.in;

  private final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
  private final ByteArrayOutputStream stdErr = new ByteArrayOutputStream();
  protected Cli cli;

  @Before
  public void setUp() throws Exception {
    // Setup necessary mock
    final JarLocation location = mock(JarLocation.class);
    when(location.getVersion()).thenReturn(Optional.of("1.0.0"));

    // Add commands you want to test

    // Redirect stdout and stderr to our byte streams
    System.setOut(new PrintStream(stdOut));
    System.setErr(new PrintStream(stdErr));

    // Build what'll run the command and interpret arguments
    cli = new Cli(location, getBootstrap(), stdOut, stdErr);
  }

  protected abstract Bootstrap getBootstrap();

  @After
  public void teardown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    System.setIn(originalIn);
  }
}
