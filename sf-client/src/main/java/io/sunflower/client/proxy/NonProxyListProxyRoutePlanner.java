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

package io.sunflower.client.proxy;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of {@link org.apache.http.conn.routing.HttpRoutePlanner}
 * that routes requests through proxy and takes into account list of hosts that should not be proxied
 */
public class NonProxyListProxyRoutePlanner extends DefaultProxyRoutePlanner {

    private static final Pattern WILDCARD = Pattern.compile("\\*");
    private static final String REGEX_WILDCARD = ".*";

    private List<Pattern> nonProxyHostPatterns;

    public NonProxyListProxyRoutePlanner(HttpHost proxy, @Nullable List<String> nonProxyHosts) {
        super(proxy, null);
        nonProxyHostPatterns = getNonProxyHostPatterns(nonProxyHosts);
    }

    public NonProxyListProxyRoutePlanner(HttpHost proxy, SchemePortResolver schemePortResolver,
                                         @Nullable List<String> nonProxyHosts) {
        super(proxy, schemePortResolver);
        this.nonProxyHostPatterns = getNonProxyHostPatterns(nonProxyHosts);
    }

    private List<Pattern> getNonProxyHostPatterns(@Nullable List<String> nonProxyHosts) {
        if (nonProxyHosts == null) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<Pattern> patterns = ImmutableList.builder();
        for (String nonProxyHost : nonProxyHosts) {
            // Replaces a wildcard to a regular expression
            patterns.add(Pattern.compile(WILDCARD.matcher(nonProxyHost).replaceAll(REGEX_WILDCARD)));
        }
        return patterns.build();
    }

    @VisibleForTesting
    protected List<Pattern> getNonProxyHostPatterns() {
        return nonProxyHostPatterns;
    }

    @Override
    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        for (Pattern nonProxyHostPattern : nonProxyHostPatterns) {
            if (nonProxyHostPattern.matcher(target.getHostName()).matches()) {
                return null;
            }
        }
        return super.determineProxy(target, request, context);
    }
}
