/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.sunflower.gizmo.utils;

import com.google.common.base.CaseFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class that contains a lot of random stuff that helps to get things done.
 *
 * @author ra
 */
public class SwissKnife {

  private static final Logger logger = LoggerFactory.getLogger(SwissKnife.class);

  /**
   * Returns the lower class name. Eg. A class named MyObject will become "myObject".
   *
   * @param object Object for which to return the lowerCamelCaseName
   * @return the lowerCamelCaseName of the Object
   */
  public static String getRealClassNameLowerCamelCase(Object object) {

    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, object.getClass().getSimpleName());

  }

  /**
   * Used to check whether a class exists on the classpath.
   *
   * @param nameWithPackage for instance com.example.conf.GloablFilters
   * @param instanceToGetClassloaderFrom usually "this" if you call this method.
   * @return true if class exists, false if not.
   */
  public static boolean doesClassExist(String nameWithPackage,
      Object instanceToGetClassloaderFrom) {
    boolean exists;

    try {
      Class.forName(nameWithPackage, false,
          instanceToGetClassloaderFrom.getClass().getClassLoader());
      exists = true;
    } catch (ClassNotFoundException e) {
      exists = false;
    }

    return exists;
  }

}
