/*
 * Copyright 2016 ninjaframework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sunflower.gizmo.standalone;

/**
 * Automatically finds a standalone to start a Ninja app. Order of preference
 * for which standalone is used taps uses the rules established in
 * StandaloneHelper.
 *
 * @see StandaloneHelper#resolveStandaloneClass()
 */
public class AutoStandalone {

    static public void main(String[] args) throws Exception {
        // either returns class or throws exception
        Class<? extends Standalone> standaloneClass
            = StandaloneHelper.resolveStandaloneClass();

        // instantiate and createInjector it!
        StandaloneHelper.create(standaloneClass).run();
    }

}
