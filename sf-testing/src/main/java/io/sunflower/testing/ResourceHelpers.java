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

import com.google.common.io.Resources;

import java.io.File;

/**
 * A set of helper methods for working with classpath resources.
 */
public class ResourceHelpers {
    private ResourceHelpers() { /* singleton */ }

    /**
     * Detects the absolute path of a class path resource.
     *
     * @param resourceClassPathLocation the filename of the class path resource
     * @return the absolute path to the denoted resource
     */
    public static String resourceFilePath(final String resourceClassPathLocation) {
        try {
            return new File(Resources.getResource(resourceClassPathLocation).toURI()).getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
