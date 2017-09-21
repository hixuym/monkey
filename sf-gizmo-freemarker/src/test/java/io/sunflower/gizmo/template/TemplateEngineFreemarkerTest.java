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
package io.sunflower.gizmo.template;

import freemarker.template.Configuration;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.inject.Singleton;

import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.Results;
import io.sunflower.gizmo.Route;
import io.sunflower.gizmo.exceptions.RenderingException;
import io.sunflower.gizmo.i18n.Lang;
import io.sunflower.gizmo.i18n.Messages;
import io.sunflower.gizmo.session.FlashScope;
import io.sunflower.gizmo.session.Session;
import io.sunflower.gizmo.utils.ResponseStreams;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerTest {

    @Mock
    Lang lang;

    @Mock
    Logger logger;

    @Mock
    TemplateEngineHelper templateEngineHelper;

    @Mock
    TemplateEngineManager templateEngineManager;

    @Mock
    TemplateEngineFreemarkerReverseRouteMethod templateEngineFreemarkerReverseRouteMethod;

    @Mock
    TemplateEngineFreemarkerAssetsAtMethod templateEngineFreemarkerAssetsAtMethod;

    @Mock
    TemplateEngineFreemarkerWebJarsAtMethod templateEngineFreemarkerWebJarsAtMethod;

    @Mock
    GizmoConfiguration configuration;

    @Mock
    Messages messages;

    @Mock
    Context context;

    @Mock
    Result result;

    @Mock
    Route route;

    TemplateEngineFreemarker templateEngineFreemarker;

    Writer writer;

    @Before
    public void before() throws Exception {
        //Setup that allows to to execute invoke(...) in a very minimal version.
        templateEngineFreemarker
            = new TemplateEngineFreemarker(
            messages,
            lang,
            logger,
            configuration,
            templateEngineHelper,
            templateEngineManager,
            templateEngineFreemarkerReverseRouteMethod,
            templateEngineFreemarkerAssetsAtMethod,
            templateEngineFreemarkerWebJarsAtMethod);


        when(lang.getLanguage(any(Context.class), any(Optional.class))).thenReturn(Optional.<String>empty());

        Session session = Mockito.mock(Session.class);
        when(session.isEmpty()).thenReturn(true);
        when(context.getSession()).thenReturn(session);
        when(context.getRoute()).thenReturn(route);
        when(lang.getLocaleFromStringOrDefault(any(Optional.class))).thenReturn(Locale.ENGLISH);

        FlashScope flashScope = Mockito.mock(FlashScope.class);
        Map<String, String> flashScopeData = new HashMap<>();
        when(flashScope.getCurrentFlashCookieData()).thenReturn(flashScopeData);
        when(context.getFlashScope()).thenReturn(flashScope);

        when(templateEngineHelper.getTemplateForResult(any(Route.class), any(Result.class), Mockito.anyString())).thenReturn("views/template.ftl.html");

        writer = new StringWriter();
        ResponseStreams responseStreams = mock(ResponseStreams.class);
        when(context.finalizeHeaders(any(Result.class))).thenReturn(responseStreams);
        when(responseStreams.getWriter()).thenReturn(writer);


    }

    @Test
    public void testThatTemplateEngineFreemarkerHasSingletonAnnotation() {
        Singleton singleton = TemplateEngineFreemarker.class.getAnnotation(Singleton.class);
        assertThat(singleton, notNullValue());
    }

    @Test
    public void testBasicInvocation() throws Exception {
        templateEngineFreemarker.invoke(context, Results.ok());
        assertThat(templateEngineFreemarker.getSuffixOfTemplatingEngine(), equalTo(".ftl.html"));
        verify(templateEngineHelper).getTemplateForResult(eq(route), any(Result.class), eq(".ftl.html"));
        assertThat(writer.toString(), equalTo("Just a plain template for testing..."));
    }

    @Test
    public void testThatConfigurationCanBeRetrieved() throws Exception {
        templateEngineFreemarker.invoke(context, Results.ok());
        assertThat(templateEngineFreemarker.getConfiguration(), CoreMatchers.notNullValue(Configuration.class));
    }

    @Test
    public void testThatWhenNotProdModeThrowsRenderingException() {
        when(templateEngineHelper.getTemplateForResult(any(Route.class), any(Result.class), Mockito.anyString())).thenReturn("views/broken.ftl.html");
        // only freemarker templates generated exceptions to browser -- it makes
        // sense that this continues in diagnostic mode only
        //when(configuration.isDev()).thenReturn(true);
        //when(configuration.areDiagnosticsEnabled()).thenReturn(true);

        try {
            templateEngineFreemarker.invoke(context, Results.ok());
            fail("exception expected");
        } catch (RenderingException e) {
            // expected
        }
    }

    @Test(expected = RuntimeException.class)
    public void testThatProdModeThrowsTemplateException() throws RuntimeException {
        when(templateEngineHelper.getTemplateForResult(any(Route.class), any(Result.class), Mockito.anyString())).thenReturn("views/broken.ftl.html");
//        when(configuration.isProd()).thenReturn(true);
        templateEngineFreemarker.invoke(context, Results.ok());
    }
}
