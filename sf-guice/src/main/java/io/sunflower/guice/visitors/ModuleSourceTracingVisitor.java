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

package io.sunflower.guice.visitors;

import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementSource;

/**
 * Visitor for logging the 'path' through which each binding was created
 */
public class ModuleSourceTracingVisitor extends DefaultElementVisitor<String> {

  @Override
  protected String visitOther(Element element) {
    Object source = element.getSource();
    ElementSource elementSource = null;
    while (source instanceof ElementSource) {
      elementSource = (ElementSource) source;
      source = elementSource.getOriginalElementSource();
    }

    if (elementSource != null) {
      return elementSource.getModuleClassNames().toString();
    }

    return null;
  }
}
