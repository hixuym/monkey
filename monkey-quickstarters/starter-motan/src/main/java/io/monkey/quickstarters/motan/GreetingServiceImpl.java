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

package io.monkey.quickstarters.motan;

import javax.inject.Singleton;

/**
 * @author Michael
 * Created at: 2019/1/4 15:54
 */
@Singleton
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String greet(String name) {
        return "Hi, " + name;
    }
}
