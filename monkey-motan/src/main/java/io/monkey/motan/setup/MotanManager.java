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

package io.monkey.motan.setup;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.ServiceConfig;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import io.monkey.lifecycle.Managed;
import io.monkey.motan.MotanFactory;

/**
 * @author Michael
 * Created at: 2019/1/15 17:05
 */
class MotanManager implements Managed {

    private final MotanFactory motanFactory;

    MotanManager(MotanFactory motanFactory) {
        this.motanFactory = motanFactory;
    }

    @Override
    public void start() {
        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
    }

    @Override
    public void stop() {
        motanFactory.getServicesConfig().values().forEach(ServiceConfig::unexport);
        motanFactory.getReferersConfig().values().forEach(RefererConfig::destroy);
        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);
    }
}
