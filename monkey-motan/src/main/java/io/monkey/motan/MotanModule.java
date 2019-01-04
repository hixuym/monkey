/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.motan;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * @author Michael
 * Created at: 2019/1/4 17:22
 */
public class MotanModule extends AbstractModule {

    private final MotanFactory motanFactory;

    public MotanModule(MotanFactory motanFactory) {
        this.motanFactory = motanFactory;
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new MotanRefererListener(motanFactory));
    }
}
