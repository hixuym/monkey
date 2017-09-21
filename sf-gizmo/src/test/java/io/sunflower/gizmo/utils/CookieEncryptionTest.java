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

package io.sunflower.gizmo.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.sunflower.gizmo.GizmoConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class CookieEncryptionTest {

    @Mock
    GizmoConfiguration configuration;
    
    @Test
    public void testThatEncryptionAndDecryptionWorksWhenEnabled() {
        String applicationSecret = SecretGenerator.generateSecret();
        when(configuration.getApplicationSecret())
               .thenReturn(applicationSecret);
        when(configuration.isCookieEncrypted())
               .thenReturn(true);
        CookieEncryption cookieEncryption = new CookieEncryption(configuration);
        
        String stringToEncrypt = "a_very_big_secret";
        String encrypted = cookieEncryption.encrypt(stringToEncrypt);
        assertThat(encrypted, not(equalTo(stringToEncrypt)));
        
        String decrypted = cookieEncryption.decrypt(encrypted);
        assertThat(decrypted, equalTo(stringToEncrypt));
    }
    
    @Test
    public void testThatEncryptionDoesNotDoAnythingWhenDisabled() {
        when(configuration.isCookieEncrypted())
               .thenReturn(false);

        CookieEncryption cookieEncryption = new CookieEncryption(configuration);
        
        String stringToEncrypt = "a_very_big_secret";
        String encrypted = cookieEncryption.encrypt(stringToEncrypt);
        assertThat(encrypted, equalTo(stringToEncrypt));
        
        String decrypted = cookieEncryption.decrypt(encrypted);
        assertThat(decrypted, equalTo(stringToEncrypt));
    }

    @Test(expected = RuntimeException.class)
    public void testThatEncryptionFailsWhenSecretEmpty() {
        String applicationSecret = "";
        when(configuration.getApplicationSecret())
               .thenReturn(applicationSecret);
        when(configuration.isCookieEncrypted())
               .thenReturn(true);
        new CookieEncryption(configuration);
    }
    
    @Test(expected = RuntimeException.class)
    public void testThatEncryptionFailsWhenSecretTooSmall() {
        String applicationSecret = "1234";
        when(configuration.getApplicationSecret())
               .thenReturn(applicationSecret);
        when(configuration.isCookieEncrypted())
               .thenReturn(true);
        new CookieEncryption(configuration);
    }
    
    
    
}
