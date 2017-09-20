/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.sunflower.gizmo.utils;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Optional;

import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.Result;

public class HttpCacheToolkitImpl implements HttpCacheToolkit {

    private static final Logger logger = LoggerFactory.getLogger(HttpCacheToolkitImpl.class);

    private final GizmoConfiguration configuration;

    @Inject
    public HttpCacheToolkitImpl(GizmoConfiguration configuration) {
        this.configuration = configuration;

    }

    public boolean isModified(Optional<String> etag, Optional<Long> lastModified, Context context) {

        final String browserEtag = context.getHeader(HttpHeaderConstants.IF_NONE_MATCH);

        if (browserEtag != null && etag.isPresent()) {
            if (browserEtag.equals(etag.get())) {
                return false;
            } else {
                return true;
            }
        }

        final String ifModifiedSince = context.getHeader(HttpHeaderConstants.IF_MODIFIED_SINCE);

        if (ifModifiedSince != null && lastModified.isPresent()) {

            if (!ifModifiedSince.isEmpty()) {
                try {
                    Date browserDate = DateUtil
                        .parseHttpDateFormat(ifModifiedSince);
                    if (browserDate.getTime() >= lastModified.get()) {
                        return false;
                    }
                } catch (IllegalArgumentException ex) {
                    logger.warn("Can't parse HTTP date", ex);
                }
                return true;
            }
        }
        return true;
    }

    public void addEtag(Context context, Result result, Long lastModified) {

        if (!configuration.isProd()) {
            result.addHeader(HttpHeaderConstants.CACHE_CONTROL, "no-cache");
        } else {
            String maxAge = configuration.getHttpCacheMaxAge();

            if (maxAge.equals("0")) {
                result.addHeader(HttpHeaderConstants.CACHE_CONTROL, "no-cache");
            } else {
                result.addHeader(HttpHeaderConstants.CACHE_CONTROL, "max-age=" + maxAge);
            }
        }

        // Use etag on demand:
        String etag = null;

        if (configuration.isUseETag()) {
            // ETag right now is only lastModified long.
            // maybe we change that in the future.
            etag = "\""
                + lastModified.toString() + "\"";
            result.addHeader(HttpHeaderConstants.ETAG, etag);

        }


        if (!isModified(Optional.ofNullable(etag), Optional.ofNullable(lastModified), context)) {

            if (context.getMethod().toLowerCase().equals("get")) {
                result.status(Result.SC_304_NOT_MODIFIED);
            }

        } else {
            result.addHeader(HttpHeaderConstants.LAST_MODIFIED,
                DateUtil.formatForHttpHeader(lastModified));

        }


    }

}
