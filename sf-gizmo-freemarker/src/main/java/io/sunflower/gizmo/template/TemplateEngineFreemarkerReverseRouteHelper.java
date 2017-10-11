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

package io.sunflower.gizmo.template;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import io.sunflower.gizmo.ReverseRouter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TemplateEngineFreemarkerReverseRouteHelper {

  private final Logger logger = LoggerFactory
      .getLogger(TemplateEngineFreemarkerReverseRouteHelper.class);

  private final ReverseRouter reverseRouter;

  @Inject
  public TemplateEngineFreemarkerReverseRouteHelper(ReverseRouter router) {
    this.reverseRouter = router;
  }

  public TemplateModel computeReverseRoute(List args) throws TemplateModelException {

    if (args.size() < 2) {

      throw new TemplateModelException(
          "Please specify at least classname and resource (2 parameters).");

    } else {

      List<String> strings = new ArrayList<>(args.size());

      for (Object o : args) {

        // We currently allow only numbers and strings as arguments
        if (o instanceof String) {
          strings.add((String) o);
        }
        if (o instanceof SimpleScalar) {
          strings.add(((SimpleScalar) o).getAsString());
        } else if (o instanceof SimpleNumber) {
          strings.add(o.toString());
        }

      }

      try {

        Class<?> clazz = Class.forName(strings.get(0));

        Object[] parameterMap = strings.subList(2, strings.size()).toArray();

        if (parameterMap.length % 2 != 0) {
          logger.error(
              "Always provide key (as String) value (as Object) pairs in parameterMap. That means providing e.g. 2, 4, 6... objects.");
          return new SimpleScalar(null);
        }

        Map<String, Object> map = new HashMap<>(parameterMap.length / 2);
        for (int i = 0; i < parameterMap.length; i += 2) {
          map.put((String) parameterMap[i], parameterMap[i + 1]);
        }

        ReverseRouter.Builder reverseRouteBuilder = reverseRouter.with(clazz, strings.get(1));

        map.forEach((name, value) -> {
          // path or query param?
          if (reverseRouteBuilder.getRoute().getParameters().containsKey(name)) {
            reverseRouteBuilder.rawPathParam(name, value);
          } else {
            reverseRouteBuilder.rawQueryParam(name, value);
          }
        });

        return new SimpleScalar(reverseRouteBuilder.build());
      } catch (ClassNotFoundException ex) {
        throw new TemplateModelException("Error. Cannot find class for String: " + strings.get(0));
      }
    }

  }
}
