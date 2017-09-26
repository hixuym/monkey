/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.sunflower.gizmo.template;

import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.Route;

/**
 * Helper methods for template engines
 *
 * @author James Roper
 */
public class TemplateEngineHelper {

    private static String VIEWS_DIR = "views";
    private static String RESOURCES_DIR = "resources";

    public String getTemplateForResult(Route route, Result result, String suffix) {
        if (result.getTemplate() == null) {
            Class resourceClass = route.getResourceClass();

            // Calculate the correct path of the template.
            // We always assume the template in the subdir "views"

            // 1) If we are in the main project =>
            // /resources/ResourceName
            // to
            // /views/ResourceName/templateName.ftl.html
            // 2) If we are in a plugin / subproject
            // =>
            // /resources/some/packages/submoduleName/ResourceName
            // to
            // views/some/packages/submoduleName/ResourceName/templateName.ftl.html

            // So let's calculate the parent package of the resource:
            String resourcePackageName = resourceClass.getPackage().getName();
            // This results in something like resources or
            // some.package.resources

            // Replace resource prefix with views prefix
            String parentPackageOfResource = resourcePackageName
                .replaceFirst(RESOURCES_DIR, VIEWS_DIR);

            // And now we rewrite everything from "." notation to directories /
            String parentResourcePackageAsPath = parentPackageOfResource
                .replaceAll("\\.", "/");

            // and the final path of the controller will be something like:
            // views/some/package/submoduleName/ResourceName/templateName.ftl.html
            return String.format("/%s/%s/%s%s", parentResourcePackageAsPath,
                resourceClass.getSimpleName(), route.getResourceMethod().getName(), suffix);
        } else {
            return result.getTemplate();
        }
    }

}
