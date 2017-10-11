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

package io.sunflower.gizmo;

import io.sunflower.gizmo.utils.ResultHandler;
import io.sunflower.guicey.lifecycle.LifecycleManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GizmoDefault implements Gizmo {

  private static final Logger logger = LoggerFactory.getLogger(GizmoDefault.class);

  @Inject
  protected LifecycleManager lifecycleManager;

  @Inject
  protected Router router;

  @Inject
  protected ResultHandler resultHandler;

  @Inject
  protected ExceptionHandler exceptionHandler;

  @Override
  public void onRouteRequest(Context.Impl context) {

    String httpMethod = context.getMethod();

    Route route = router.getRouteFor(httpMethod, context.getRequestPath());

    if (route == null) {
      // throw a 404 "not found" because we did not find the route
      Result result = exceptionHandler.getNotFoundResult(context);
      renderErrorResultAndCatchAndLogExceptions(result, context);

      return;
    }

    context.setRoute(route);

    Result underlyingResult = null;

    try {

      underlyingResult = route.getFilterChain().next(context);

      resultHandler.handleResult(underlyingResult, context);

    } catch (Exception exception) {
      // call special handler to capture the underlying result if there is one
      Result result = exceptionHandler.onException(context, exception, underlyingResult);
      renderErrorResultAndCatchAndLogExceptions(result, context);
    } finally {
      context.cleanup();
    }
  }

  @Override
  public void renderErrorResultAndCatchAndLogExceptions(Result errorResult, Context context) {

    try {
      resultHandler.handleResult(errorResult, context);
    } catch (Exception exceptionCausingRenderError) {
      logger.error("Unable to handle result. That's really really fishy.",
          exceptionCausingRenderError);
    }
  }

  @Override
  public void onFrameworkStart() {
    lifecycleManager.start();
  }

  @Override
  public void onFrameworkShutdown() {
    lifecycleManager.stop();
  }
}
