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

package io.sunflower.guice;

import com.google.inject.Module;

import java.util.List;

/**
 * ModuleProcessor
 *
 * @author michael
 * created on 17/11/2 17:06
 */
public interface ModuleProcessor {

    /**
     * process module before injector created.
     * @param modulesLoaded
     */
    void process(List<Module> modulesLoaded);
}
