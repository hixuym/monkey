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

package io.sunflower.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sunflower.Mode;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.sunflower.ModeHelper.determineModeFromSystemPropertiesOrDevIfNotSet;

/**
 * AbstractServerFactory
 *
 * @author michael
 * created on 17/10/28 21:50
 */
public abstract class AbstractServerFactory implements ServerFactory {

    private Mode mode;

    @NotNull
    private Map<String, String> serverProperties = new LinkedHashMap<>(20);

    @Override
    @JsonProperty("properties")
    public Map<String, String> getServerProperties() {
        return this.serverProperties;
    }

    @JsonProperty("properties")
    public void setServerProperties(Map<String, String> serverProperties) {
        this.serverProperties = serverProperties;
    }

    @JsonProperty
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    @JsonProperty
    public Mode getMode() {
        if (this.mode == null) {
            this.mode = determineModeFromSystemPropertiesOrDevIfNotSet();
        }
        return this.mode;
    }
}
