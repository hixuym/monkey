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

package io.monkey.motan.config;

import com.weibo.api.motan.config.MethodConfig;

/**
 * @author Michael
 * Created at: 2019/1/4 12:55
 */
public class MethodConfigFactory {
    // 方法名
    private String name;
    // 超时时间
    private Integer requestTimeout;
    // 失败重试次数（默认为0，不重试）
    private Integer retries;
    // 最大并发调用
    // TODO 暂未实现
    private Integer actives;
    // 参数类型（逗号分隔）
    private String argumentTypes;

    MethodConfig build() {

        MethodConfig methodConfig = new MethodConfig();

        methodConfig.setActives(actives);
        methodConfig.setName(name);
        methodConfig.setRequestTimeout(requestTimeout);
        methodConfig.setArgumentTypes(argumentTypes);

        return methodConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getActives() {
        return actives;
    }

    public void setActives(Integer actives) {
        this.actives = actives;
    }

    public String getArgumentTypes() {
        return argumentTypes;
    }

    public void setArgumentTypes(String argumentTypes) {
        this.argumentTypes = argumentTypes;
    }
}
