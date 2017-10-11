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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import io.sunflower.gizmo.utils.MethodReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterImpl implements Router {

  static private final Logger logger = LoggerFactory.getLogger(RouterImpl.class);

  private final GizmoConfiguration configuration;
  private final List<RouteBuilderImpl> allRouteBuilders = new ArrayList<>();
  private final Injector injector;
  private List<Route> routes;
  // for fast reverse route lookups
  private Map<MethodReference, Route> reverseRoutes;
  // This regex works for both {myParam} AND {myParam: .*} (with regex)
  private final String VARIABLE_PART_PATTERN_WITH_PLACEHOLDER = "\\{(%s)(:\\s([^}]*))?\\}";
  private final Provider<RouteBuilderImpl> routeBuilderImplProvider;

  @Inject
  public RouterImpl(Injector injector,
      GizmoConfiguration configuration,
      Provider<RouteBuilderImpl> routeBuilderImplProvider) {
    this.injector = injector;
    this.configuration = configuration;
    this.routeBuilderImplProvider = routeBuilderImplProvider;
  }

  @Override
  public Route getRouteFor(String httpMethod, String uri) {
    if (routes == null) {
      throw new IllegalStateException(
          "Attempt to get route when routes not compiled");
    }

    for (Route route : routes) {
      if (route.matches(httpMethod, uri)) {
        return route;
      }
    }

    return null;

  }

  @Override
  public void compileRoutes() {
    if (routes != null) {
      throw new IllegalStateException("Routes already compiled");
    }
    List<Route> routesLocal = new ArrayList<>();

    for (RouteBuilderImpl routeBuilder : allRouteBuilders) {
      routesLocal.add(routeBuilder.buildRoute(injector));
    }

    this.routes = ImmutableList.copyOf(routesLocal);

    // compile reverse routes for O(1) lookups
    this.reverseRoutes = new HashMap<>(this.routes.size());

    for (Route route : this.routes) {
      // its possible this route is a Result instead of a resource method
      if (route.getResourceClass() != null) {
        MethodReference resourceMethodRef
            = new MethodReference(
            route.getResourceClass(),
            route.getResourceMethod().getName());

        if (this.reverseRoutes.containsKey(resourceMethodRef)) {
          // the first one wins with reverse routing so we ignore this route
        } else {
          this.reverseRoutes.put(resourceMethodRef, route);
        }
      }
    }

    logRoutes();
  }

  @Override
  public List<Route> getRoutes() {
    verifyRoutesCompiled();
    return routes;
  }

  @Override
  public RouteBuilder GET() {

    RouteBuilderImpl routeBuilder = routeBuilderImplProvider.get().GET();
    allRouteBuilders.add(routeBuilder);

    return routeBuilder;
  }

  @Override
  public RouteBuilder POST() {
    RouteBuilderImpl routeBuilder = routeBuilderImplProvider.get().POST();
    allRouteBuilders.add(routeBuilder);

    return routeBuilder;
  }

  @Override
  public RouteBuilder PUT() {
    RouteBuilderImpl routeBuilder = routeBuilderImplProvider.get().PUT();
    allRouteBuilders.add(routeBuilder);

    return routeBuilder;
  }

  @Override
  public RouteBuilder DELETE() {
    RouteBuilderImpl routeBuilder = routeBuilderImplProvider.get().DELETE();
    allRouteBuilders.add(routeBuilder);

    return routeBuilder;
  }

  @Override
  public RouteBuilder OPTIONS() {
    RouteBuilderImpl routeBuilder = routeBuilderImplProvider.get().OPTIONS();
    allRouteBuilders.add(routeBuilder);

    return routeBuilder;
  }

  @Override
  public RouteBuilder HEAD() {
    RouteBuilderImpl routeBuilder = routeBuilderImplProvider.get().HEAD();
    allRouteBuilders.add(routeBuilder);

    return routeBuilder;
  }

  @Override
  public RouteBuilder METHOD(String method) {
    RouteBuilderImpl routeBuilder = routeBuilderImplProvider.get().METHOD(method);
    allRouteBuilders.add(routeBuilder);

    return routeBuilder;
  }

  private void verifyRoutesCompiled() {
    if (routes == null) {
      throw new IllegalStateException(
          "Routes not compiled!");
    }
  }

  @Override
  public Optional<Route> getRouteForResourceClassAndMethod(
      Class<?> resourceClass,
      String resourceMethodName) {

    verifyRoutesCompiled();

    MethodReference reverseRouteKey
        = new MethodReference(resourceClass, resourceMethodName);

    Route route = this.reverseRoutes.get(reverseRouteKey);

    return Optional.ofNullable(route);
  }

  private void logRoutes() {
    // determine the width of the columns
    int maxMethodLen = 0;
    int maxPathLen = 0;
    int maxResourceLen = 0;

    for (Route route : getRoutes()) {

      maxMethodLen = Math.max(maxMethodLen, route.getHttpMethod().length());
      maxPathLen = Math.max(maxPathLen, route.getUri().length());

      if (route.getResourceClass() != null) {

        int resourceLen = route.getResourceClass().getName().length()
            + route.getResourceMethod().getName().length();
        maxResourceLen = Math.max(maxResourceLen, resourceLen);

      }

    }

    // log the routing table
    int borderLen = 10 + maxMethodLen + maxPathLen + maxResourceLen;
    String border = Strings.padEnd("", borderLen, '-');

    logger.info(border);
    logger.info("Registered routes");
    logger.info(border);

    for (Route route : getRoutes()) {

      if (route.getResourceClass() != null) {

        logger.info("{} {}  =>  {}.{}()",
            Strings.padEnd(route.getHttpMethod(), maxMethodLen, ' '),
            Strings.padEnd(route.getUri(), maxPathLen, ' '),
            route.getResourceClass().getName(),
            route.getResourceMethod().getName());

      } else {

        logger.info("{} {}", route.getHttpMethod(), route.getUri());

      }

    }

  }
}