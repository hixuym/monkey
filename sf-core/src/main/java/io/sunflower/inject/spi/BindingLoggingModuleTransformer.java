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

package io.sunflower.inject.spi;

import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author michael
 */
public class BindingLoggingModuleTransformer implements ModuleListTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(BindingLoggingModuleTransformer.class);

    @Override
    public List<Module> transform(List<Module> modules) {
        for (Element binding : Elements.getElements(Stage.TOOL, modules)) {
            LOG.debug("Binding : {}", binding);
        }

        return modules;
    }
}
