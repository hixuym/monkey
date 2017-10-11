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

package io.sunflower.example;

import static io.sunflower.testing.ResourceHelpers.resourceFilePath;
import static org.junit.Assert.assertNotNull;

import io.ebean.EbeanServer;
import io.sunflower.example.core.UserMapper;
import io.sunflower.testing.ConfigOverride;
import io.sunflower.testing.junit.SunflowerAppRule;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class UserMapperTests {

  @ClassRule
  public static SunflowerAppRule<ExampleConfiguration> appRule =
      new SunflowerAppRule<>(ExampleApplication.class,
          resourceFilePath("example.yml"),
          ConfigOverride.config("server.type", "default"));

  @Inject
  private EbeanServer ebeanServer;

  @Inject
  private UserMapper userMapper;

  @Before
  public void init() {
    appRule.inject(this);
  }

  @Test
  public void testUserMapper() {
    assertNotNull(userMapper);
  }

  @Test
  public void testEbeanServer() {
    assertNotNull(ebeanServer);
  }
}
