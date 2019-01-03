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

package io.monkey.resteasy.gzip;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

/**
 * GZipFeature
 *
 * @author michael
 * created on 17/11/9 11:25
 */
@Provider
public class GZipFeature implements Feature {

    private boolean forceEncoding;

    public GZipFeature(boolean forceEncoding) {
        this.forceEncoding = forceEncoding;
    }

    @Override
    public boolean configure(FeatureContext context) {

        context.register(new ConfiguredGZipEncoder(forceEncoding));
        context.register(new GZipDecoder());

        return true;
    }
}
