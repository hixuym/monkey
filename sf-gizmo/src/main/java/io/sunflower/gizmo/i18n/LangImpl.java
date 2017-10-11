/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.sunflower.gizmo.i18n;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.Cookie;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.utils.GizmoConstant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LangImpl implements Lang {

  private static Logger logger = LoggerFactory.getLogger(LangImpl.class);

  private final String applicationCookiePrefix;

  private final int TEN_YEARS = 60 * 60 * 24 * 365 * 10;

  private final GizmoConfiguration configuration;

  private final String DEFAULT_LANGUAGE;


  @Inject
  public LangImpl(GizmoConfiguration configuration) {

    this.configuration = configuration;

    this.applicationCookiePrefix = configuration.getCookiePrefix();

    this.DEFAULT_LANGUAGE = getDefaultLanguage(this.configuration);

  }


  @Override
  public void clearLanguage(Result result) {

    Cookie defaultLangCookie = generateLanguageCookie();
    result.unsetCookie(defaultLangCookie.getName());

  }


  @Override
  public Result setLanguage(String locale, Result result) {

    Cookie defaultLangCookie = generateLanguageCookie();
    Cookie cookie = Cookie.builder(defaultLangCookie).setValue(locale).build();
    result.addCookie(cookie);

    return result;

  }


  @Override
  public Optional<String> getLanguage(Context context, Optional<Result> result) {

    Cookie defaultCookie = generateLanguageCookie();

    // Step 1: Determine language from result.
    // Result always has priority over context and will overwrite context.
    if (result.isPresent()) {
      Cookie cookie = result.get().getCookie(defaultCookie.getName());

      if (cookie != null) {

        if (cookie.getValue() != null
            && !cookie.getValue().isEmpty()) {

          //forced language is:
          return Optional.of(cookie.getValue());
        }

      }
    }

    // Step 2 => we did not find the language in the result
    // We try to determine it from the context.
    Cookie cookie = context.getCookie(defaultCookie.getName());

    if (cookie != null) {

      if (cookie.getValue() != null
          && !cookie.getValue().isEmpty()) {
        //forced language is:
        return Optional.of(cookie.getValue());
      }

    }

    // Step 3: Determine language from Accept-Language header.
    String acceptLanguage = context.getAcceptLanguage();
    if (acceptLanguage == null) {
      return Optional.empty();
    }

    // Check if we get a registered mapping for the language input string.
    // At that point the language may be either language-country or only country.
    // extract multiple languages from Accept-Language header
    Iterable<String> languages = Splitter.on(",").trimResults().split(acceptLanguage);

    for (String language : languages) {
      // Ignore the relative quality factor in Accept-Language header
      if (language.contains(";")) {
        language = language.split(";")[0];

        return Optional.of(language);

      } else {

        return Optional.of(language);

      }

    }

    return Optional.empty();

  }


  /**
   * Retrieves the language cookie or generates one with a very long max age (ten years).
   *
   * @return The cookie
   */
  private Cookie generateLanguageCookie() {

    Cookie cookie = Cookie.builder(applicationCookiePrefix
        + GizmoConstant.LANG_COOKIE_SUFFIX, "")
        .setMaxAge(TEN_YEARS).build();

    return cookie;

  }

  @Override
  public boolean isLanguageDirectlySupportedByThisApplication(String language) {

    List<String> applicationLanguages = configuration.getApplicationLangs();

    for (String applicationLanguage : applicationLanguages) {
      if (applicationLanguage.equals(language)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public Locale getLocaleFromStringOrDefault(Optional<String> language) {

    if (language.isPresent()) {

      return Locale.forLanguageTag(language.get());

    } else {

      return Locale.forLanguageTag(DEFAULT_LANGUAGE);

    }

  }


  String getDefaultLanguage(GizmoConfiguration configuration) {

    List<String> applicationLanguages
        = configuration.getApplicationLangs();

    if (applicationLanguages == null || applicationLanguages.size() == 0) {

      String EXCEPTION_TEXT =
          "Can not retrieve application languages from configuration."
              + " Did you forget to define at least one language in your application.conf file?"
              + " For instance 'application.languages=en' makes 'en' your default language.";

      throw new IllegalStateException(EXCEPTION_TEXT);

    }

    // by convention the first language is the default language

    return applicationLanguages.get(0);

  }

}
