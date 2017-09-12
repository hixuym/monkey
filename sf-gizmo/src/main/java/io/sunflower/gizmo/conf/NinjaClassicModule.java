/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.gizmo.conf;

import com.google.inject.AbstractModule;

import io.sunflower.gizmo.bodyparser.BodyParserEngineJson;
import io.sunflower.gizmo.bodyparser.BodyParserEnginePost;
import io.sunflower.gizmo.server.GizmoConfiguration;
import io.sunflower.gizmo.template.TemplateEngineFreemarker;
import io.sunflower.gizmo.template.TemplateEngineJson;
import io.sunflower.gizmo.template.TemplateEngineJsonP;
import io.sunflower.gizmo.template.TemplateEngineText;

/**
 * The classic configuration of the ninja framework (jackson, freemarker,
 * postoffice, etc.)
 */
public class NinjaClassicModule extends AbstractModule {

    private final GizmoConfiguration ninjaProperties;
    private boolean freemarker;
    private boolean json;
    private boolean xml;
    private boolean postoffice;
    private boolean cache;
    private boolean migrations;
    private boolean jpa;
    private boolean scheduler;

    public NinjaClassicModule(GizmoConfiguration ninjaProperties) {
        this(ninjaProperties, true);
    }

    public NinjaClassicModule(GizmoConfiguration ninjaProperties, boolean defaultEnabled) {
        this.ninjaProperties = ninjaProperties;
        this.freemarker = defaultEnabled;
        this.json = defaultEnabled;
        this.xml = defaultEnabled;
        this.postoffice = defaultEnabled;
        this.cache = defaultEnabled;
        this.migrations = defaultEnabled;
        this.jpa = defaultEnabled;
        this.scheduler = defaultEnabled;
    }

    public NinjaClassicModule freemarker(boolean enabled) {
        this.freemarker = enabled;
        return this;
    }

    public NinjaClassicModule json(boolean enabled) {
        this.json = enabled;
        return this;
    }

    public NinjaClassicModule xml(boolean enabled) {
        this.xml = enabled;
        return this;
    }

    public NinjaClassicModule postoffice(boolean enabled) {
        this.postoffice = enabled;
        return this;
    }

    public NinjaClassicModule cache(boolean enabled) {
        this.cache = enabled;
        return this;
    }

    public NinjaClassicModule migrations(boolean enabled) {
        this.migrations = enabled;
        return this;
    }

    public NinjaClassicModule jpa(boolean enabled) {
        this.jpa = enabled;
        return this;
    }

    public NinjaClassicModule scheduler(boolean scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    @Override
    public void configure() {
        // NOTE: these are grouped to line up with third-party dependencies
        // (e.g. jackson supports templates & body parsers)

        // Text & post require no 3rd party libs
        bind(TemplateEngineText.class);
        bind(BodyParserEnginePost.class);

        // Freemarker
        if (freemarker) {
            bind(TemplateEngineFreemarker.class);
        }

        // Jackson json support
        if (json) {
            bind(TemplateEngineJson.class);
            bind(TemplateEngineJsonP.class);
            bind(BodyParserEngineJson.class);
        }
    }

}
