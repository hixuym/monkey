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

import com.google.common.io.BaseEncoding;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.sunflower.gizmo.GizmoConfiguration;

@Singleton
public class Crypto {

    private final String applicationSecret;

    /**
     * Secret is a secret key. Usually something like: "Fxu6U5BTGIJZ06c8bD1xkhHc3Ct5JZXlst8tJ1K5uJJPaLdceDo6CUz0iWpjjQUY".
     */
    @Inject
    public Crypto(GizmoConfiguration configuration) {
        this.applicationSecret = configuration.getApplicationSecret();
    }

    public String signHmacSha1(String message) {

        return signHmacSha1(message, applicationSecret);

    }

    private String signHmacSha1(String value, String key) {
        try {

            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

            // Convert raw bytes to Hex
            // Convert array of Hex bytes to a String
            return BaseEncoding.base16().lowerCase().encode(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
