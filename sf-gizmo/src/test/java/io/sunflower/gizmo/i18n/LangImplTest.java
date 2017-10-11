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

package io.sunflower.gizmo.i18n;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.Cookie;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.Results;
import io.sunflower.gizmo.utils.GizmoConstant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LangImplTest {

  @Mock
  private GizmoConfiguration configuration;

  @Mock
  private Context context;

  @Captor
  ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

  @Before
  public void before() {
    when(configuration.getApplicationLangs()).thenReturn(Arrays.asList("en"));
  }


  @Test
  public void testGetLanguage() {

    Cookie cookie = Cookie.builder("NINJA_TEST" + GizmoConstant.LANG_COOKIE_SUFFIX, "de").build();

    when(configuration.getCookiePrefix()).thenReturn("NINJA_TEST");
    when(context.getCookie("NINJA_TEST" + GizmoConstant.LANG_COOKIE_SUFFIX)).thenReturn(cookie);

    Lang lang = new LangImpl(configuration);

    // 1) with context and result => but result does not have a default lang
    Result result = Results.ok();
    Optional<String> language = lang.getLanguage(context, Optional.of(result));
    assertEquals("de", language.get());

    // 2) with context and result => result has already new lang set...
    result = Results.ok();
    cookie = Cookie.builder("NINJA_TEST" + GizmoConstant.LANG_COOKIE_SUFFIX, "en").build();
    result.addCookie(cookie);

    language = lang.getLanguage(context, Optional.of(result));
    assertEquals("en", language.get());


  }

  @Test
  public void testChangeLanguage() {

    Cookie cookie = Cookie.builder("NINJA_TEST" + GizmoConstant.LANG_COOKIE_SUFFIX, "de").build();
    when(configuration.getCookiePrefix()).thenReturn("NINJA_TEST");

    Lang lang = new LangImpl(configuration);

    // test with result
    Result result = Results.noContent();

    result = lang.setLanguage("to", result);
    assertEquals("to", result.getCookie(cookie.getName()).getValue());
    assertEquals(Result.SC_204_NO_CONTENT, result.getStatusCode());


  }

  @Test
  public void testClearLanguage() {

    Cookie cookie = Cookie.builder("NINJA_TEST" + GizmoConstant.LANG_COOKIE_SUFFIX, "de").build();

    when(configuration.getCookiePrefix()).thenReturn("NINJA_TEST");

    Lang lang = new LangImpl(configuration);

    Result result = Results.ok();

    lang.clearLanguage(result);

    Cookie returnCookie = result.getCookie(cookie.getName());
    assertEquals("", returnCookie.getValue());
    assertEquals(0, returnCookie.getMaxAge());

  }

  @Test
  public void testGetLocaleFromStringOrDefault() {

    // ONE DEFAULT LOCALE
    when(configuration.getApplicationLangs()).thenReturn(Arrays.asList("en"));
    Lang lang = new LangImpl(configuration);

    Optional<String> language = Optional.empty();
    Locale locale = lang.getLocaleFromStringOrDefault(language);

    assertEquals(Locale.ENGLISH, locale);

    // GERMAN LOCALE
    when(configuration.getApplicationLangs()).thenReturn(Arrays.asList("de", "en"));
    lang = new LangImpl(configuration);

    language = Optional.empty();
    locale = lang.getLocaleFromStringOrDefault(language);

    assertEquals(Locale.GERMAN, locale);

    // GERMANY LOCALE
    when(configuration.getApplicationLangs()).thenReturn(Arrays.asList("de-DE", "en"));
    lang = new LangImpl(configuration);

    language = Optional.empty();
    locale = lang.getLocaleFromStringOrDefault(language);

    assertEquals(Locale.GERMANY, locale);


  }


  @Test(expected = IllegalStateException.class)
  public void testGetLocaleFromStringOrDefaultISEWhenNoApplicationLanguageDefined() {

    // ONE DEFAULT LOCALE
    when(configuration.getApplicationLangs()).thenReturn(Arrays.asList());
    Lang lang = new LangImpl(configuration);

    Optional<String> language = Optional.empty();
    lang.getLocaleFromStringOrDefault(language);

    // ISE expected

  }

}