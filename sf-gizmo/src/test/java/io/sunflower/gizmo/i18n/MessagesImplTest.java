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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.Cookie;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.Results;
import io.sunflower.util.Dates;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessagesImplTest {

  @Mock
  private GizmoConfiguration configuration;

  @Mock
  Context context;

  Result result;

  @Test
  public void testGetWithLanguage() {

    when(configuration.getApplicationLangs())
        .thenReturn(Arrays.asList("en", "de", "fr-FR"));

    Lang lang = new LangImpl(configuration);
    Messages messages = new MessagesImpl(lang, configuration);

    // that will refer to messages_en.properties:
    assertEquals("english", messages.get("language", Optional.of("en-US")).get());
    assertEquals("english", messages.get("language", Optional.of("en-CA")).get());
    assertEquals("english", messages.get("language", Optional.of("en-UK")).get());

    // that will refer to messages_de.properties:
    assertEquals("deutsch", messages.get("language", Optional.of("de")).get());
    assertEquals("deutsch", messages.get("language", Optional.of("de-DE")).get());

    // that will refer to messages_fr-FR.properties:
    assertEquals("français", messages.get("language", Optional.of("fr-FR")).get());

    // that will refer to messages_fr-FR.properties:
    assertEquals("français", messages.get("language", Optional.of("da,fr-FR;q=0.8")).get());
    assertEquals("français", messages.get("language", Optional.of("da;q=0.9, fr-FR; q=0.8")).get());

    // that will refer to messages_de.properties:
    assertEquals("deutsch", messages.get("language", Optional.of("de,fr-FR;q=0.8")).get());
    assertEquals("deutsch", messages.get("language", Optional.of("de;q=0.9, fr-FR; q=0.8")).get());

    assertEquals("defaultlanguage", messages.get("language", Optional.of("fr")).get());

    assertEquals(Optional.empty(), messages.get("a_non_existing_key", Optional.of("fr")));
  }

  @Test
  public void testGetWithContextAndResult() {

    when(configuration.getApplicationLangs())
        .thenReturn(Arrays.asList("en", "de", "fr-FR"));

    Lang lang = new LangImpl(configuration);
    Messages messages = new MessagesImpl(lang, configuration);

    result = Results.ok();

    // test with context Accept Header
    when(context.getAcceptLanguage()).thenReturn("en-US");
    assertEquals("english", messages.get("language", context, Optional.of(result)).get());
    when(context.getAcceptLanguage()).thenReturn("en-CA");
    assertEquals("english", messages.get("language", context, Optional.of(result)).get());
    when(context.getAcceptLanguage()).thenReturn("en-UK");
    assertEquals("english", messages.get("language", context, Optional.of(result)).get());

    // test that result overwrites context AcceptHeader
    lang.setLanguage("de", result);
    assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());
    result = Results.ok();
    lang.setLanguage("de-DE", result);
    assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());

    // that forced language from context works with empty result
    result = Results.ok();
    when(context.getCookie(Mockito.anyString())).thenReturn(
        Cookie.builder("name", "fr-FR").build());
    assertEquals("français", messages.get("language", context, Optional.of(result)).get());
    //and the result overwrites it again...
    result = Results.ok();
    lang.setLanguage("de-DE", result);
    assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());


  }

  @Test
  public void testGetWithLanguageAndParameters() {
    when(configuration.getApplicationLangs())
        .thenReturn(Arrays.asList("en", "de", "fr-FR"));

    Lang lang = new LangImpl(configuration);

    Messages messages = new MessagesImpl(lang, configuration);

    // that will refer to messages_en.properties:
    assertEquals("this is the placeholder: test_parameter",
        messages.get("message_with_placeholder", Optional.of("en-US"), "test_parameter").get());
    assertEquals("this is the placeholder: test_parameter",
        messages.get("message_with_placeholder", Optional.of("en-CA"), "test_parameter").get());
    assertEquals("this is the placeholder: test_parameter",
        messages.get("message_with_placeholder", Optional.of("en-UK"), "test_parameter").get());

    // that will refer to messages_de.properties:
    assertEquals("Toröööö - das ist der platzhalter: test_parameter",
        messages.get("message_with_placeholder", Optional.of("de"), "test_parameter").get());
    assertEquals("Toröööö - das ist der platzhalter: test_parameter",
        messages.get("message_with_placeholder", Optional.of("de-DE"), "test_parameter").get());

  }

  @Test
  public void testGetWithContextAndResultAndParameters() {
    when(configuration.getApplicationLangs())
        .thenReturn(Arrays.asList("en", "de", "fr-FR"));
    Lang lang = new LangImpl(configuration);
    Messages messages = new MessagesImpl(lang, configuration);
    result = Results.ok();

    when(context.getAcceptLanguage()).thenReturn("en-US");
    assertEquals("this is the placeholder: test_parameter",
        messages.getWithDefault("message_with_placeholder", "default value", context,
            Optional.of(result), "test_parameter"));

    when(context.getAcceptLanguage()).thenReturn("fr-FR");
    assertEquals("c'est le placeholder: test_parameter",
        messages.getWithDefault("message_with_placeholder", "default value", context,
            Optional.of(result), "test_parameter"));

    when(context.getAcceptLanguage()).thenReturn("fr-FR");
    assertEquals("c'est le message default: test_parameter",
        messages.getWithDefault("i_do_not_exist", "c''est le message default: {0}", context,
            Optional.of(result), "test_parameter"));

  }

  @Test
  public void testGetWithDefaultAndLanguage() {
    when(configuration.getApplicationLangs())
        .thenReturn(Arrays.asList("en", "de", "fr-FR"));

    Lang lang = new LangImpl(configuration);
    Messages messages = new MessagesImpl(lang, configuration);

    assertEquals("this is the placeholder: test_parameter",
        messages.getWithDefault("message_with_placeholder", "default value", Optional.of("en-US"),
            "test_parameter"));

    assertEquals("c'est le placeholder: test_parameter",
        messages.getWithDefault("message_with_placeholder", "default value", Optional.of("fr-FR"),
            "test_parameter"));

    assertEquals("c'est le message default: test_parameter",
        messages.getWithDefault("i_do_not_exist", "c''est le message default: {0}",
            Optional.of("fr-FR"), "test_parameter"));
  }

  @Test
  public void testGetWithDefaultAndContextAndResult() {
    when(configuration.getApplicationLangs())
        .thenReturn(Arrays.asList("en", "de", "fr-FR"));

    Lang lang = new LangImpl(configuration);
    Messages messages = new MessagesImpl(lang, configuration);

    result = Results.ok();

    // test with context Accept Header
    when(context.getAcceptLanguage()).thenReturn("en-US");

    // that will refer to messages_en.properties:
    assertEquals("this is the placeholder: test_parameter",
        messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter")
            .get());
    when(context.getAcceptLanguage()).thenReturn("en-CA");
    assertEquals("this is the placeholder: test_parameter",
        messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter")
            .get());
    when(context.getAcceptLanguage()).thenReturn("en-UK");
    assertEquals("this is the placeholder: test_parameter",
        messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter")
            .get());

    // that will refer to messages_de.properties:
    lang.setLanguage("de", result);
    assertEquals("Toröööö - das ist der platzhalter: test_parameter",
        messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter")
            .get());

    lang.setLanguage("de-DE", result);
    assertEquals("Toröööö - das ist der platzhalter: test_parameter",
        messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter")
            .get());

    // that forced language from context works with empty result
    result = Results.ok();
    when(context.getCookie(Mockito.anyString())).thenReturn(
        Cookie.builder("name", "fr-FR").build());
    assertEquals("c'est le placeholder: test_parameter",
        messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter")
            .get());
    //and the result overwrites it again...
    result = Results.ok();
    lang.setLanguage("de-DE", result);
    assertEquals("Toröööö - das ist der platzhalter: test_parameter",
        messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter")
            .get());
  }

  @Test
  public void testGetWithSpecialI18nPlaceholder() {
    when(configuration.getApplicationLangs())
        .thenReturn(Arrays.asList("en", "de", "fr-FR"));

    Date DATE_1970_JAN = Dates.asUtilDate(LocalDateTime.of(1970, 1, 1, 1, 1));

    Lang lang = new LangImpl(configuration);
    Messages messages = new MessagesImpl(lang, configuration);

    // test fallback to default (english in that case)
    Optional<String> language = Optional.empty();
    Optional<String> result = messages
        .get("message_with_placeholder_date", language, DATE_1970_JAN);

    assertEquals("that's a date: Jan 1, 1970", result.get());

    // de as language
    language = Optional.of("de");
    result = messages.get("message_with_placeholder_date", language, DATE_1970_JAN);

    assertEquals("das ist ein datum: 01.01.1970", result.get());

    // fr as language
    language = Optional.of("fr-FR");
    result = messages.get("message_with_placeholder_date", language, DATE_1970_JAN);

    assertEquals("c'est la date: 1 janv. 1970", result.get());

    // en as language
    language = Optional.of("en");
    result = messages.get("message_with_placeholder_date", language, DATE_1970_JAN);

    assertEquals("that's a date: Jan 1, 1970", result.get());
  }


  @Test
  public void testCorrectParsingOfDelimitersInPropertiesFiles() {

    when(configuration.getApplicationLangs())
        .thenReturn(Arrays.asList("en", "de", "fr-FR"));
    Lang lang = new LangImpl(configuration);
    Messages messages = new MessagesImpl(lang, configuration);

    assertEquals("prop1, prop2, prop3",
        messages.get("a_propert_with_commas", Optional.of("en-US")).get());
  }

}
