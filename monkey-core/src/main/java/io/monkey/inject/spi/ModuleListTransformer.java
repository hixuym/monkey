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

package io.monkey.inject.spi;

import com.google.inject.Module;

import java.util.List;

/**
 * Module transformers are used to modify the final list of modules and return a modified or
 * augmented list of modules which may included additional or removed bindings.
 * <p>
 * ModuleTransfomers can be used to do the following types of functionality, 1.  Remove unwanted
 * bindings 2.  Auto add non-existent bindings 3.  Warn on dangerous bindings like toInstance() and
 * static injection.
 * @author michael
 */
public interface ModuleListTransformer {

    /**
     * Using the provided list of modules (and bindings) return a new augments list which may included
     * additional bindings and modules.
     *
     * @return New list of modules.  Can be the old list.
     */
    List<Module> transform(List<Module> modules);
}
