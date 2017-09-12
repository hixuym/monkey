/*
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sunflower.gizmo.utils;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.sunflower.gizmo.server.GizmoConfiguration;

import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class NinjaBaseDirectoryResolverTest {

    @Mock
    GizmoConfiguration ninjaProperties;

    @Test
    public void testThatCorrectValueIsReturnedWithDefaultSetup() {
        NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties);

        String result = ninjaBaseDirectoryResolver.resolveApplicationClassName("conf.Filters");

        assertThat(result, Matchers.equalTo("conf.Filters"));
    }

    @Test
    public void testThatCorrectValueIsReturnedWithDifferentBasePackage() {
        Mockito.when(ninjaProperties.getApplicationModulesBasePackage()).thenReturn("com.example");
        NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties);

        String result = ninjaBaseDirectoryResolver.resolveApplicationClassName("conf.Filters");

        assertThat(result, Matchers.equalTo("com.example.conf.Filters"));
    }

}
