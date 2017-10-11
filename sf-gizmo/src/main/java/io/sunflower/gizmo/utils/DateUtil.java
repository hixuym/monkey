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

package io.sunflower.gizmo.utils;

import io.sunflower.util.Dates;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

  /**
   * Can be used to format a date into http header compatible strings.
   *
   * It can be used to generate something like: Date: Wed, 05 Sep 2012 09:16:19 GMT Expires: Thu, 01 Jan 1970 00:00:00
   * GMT
   *
   * @param date The date to format
   * @return a http header compatible string like "Thu, 01 Jan 1970 00:00:00 GMT"
   */
  public static String formatForHttpHeader(Date date) {
    Instant instant = Dates.asInstant(date);
    return DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT")).format(instant);
  }

  /**
   * Can be used to format a unix timestamp into http header compatible strings.
   *
   * It can be used to generate something like: Date: Wed, 05 Sep 2012 09:16:19 GMT Expires: Thu, 01 Jan 1970 00:00:00
   * GMT
   *
   * @param unixTime The long (unixtime) to format
   * @return a http header compatible string like "Thu, 01 Jan 1970 00:00:00 GMT"
   */
  public static String formatForHttpHeader(Long unixTime) {

    return DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"))
        .format(Instant.ofEpochMilli(unixTime));
  }


  /**
   * Can be used to parse http times. For instance something like a http header Date: Tue, 26 Mar 2013 13:47:13 GMT
   *
   * INFO: consider the JodaTime based DateUtil.parseHttpDateFormatToDateTime(...) version
   *
   * @param httpDateFormat in http format: Date: Tue, 26 Mar 2013 13:47:13 GMT
   * @return A nice "Date" object containing that http timestamp.
   * @throws ParseException If something goes wrong.
   */
  public static Date parseHttpDateFormat(String httpDateFormat) throws IllegalArgumentException {
    return Dates
        .asUtilDate(LocalDateTime.parse(httpDateFormat, DateTimeFormatter.RFC_1123_DATE_TIME),
            ZoneId.of("GMT"));
  }
}
