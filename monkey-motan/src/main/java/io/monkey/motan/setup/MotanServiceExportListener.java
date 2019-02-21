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

import com.google.common.base.Strings;
import com.google.inject.spi.InjectionListener;
import com.weibo.api.motan.config.ServiceConfig;
import io.monkey.motan.MotanFactory;
import io.monkey.motan.annotation.MotanService;
import io.monkey.setup.Environment;

/**
 * @author Michael
 * Created at: 2019/1/4 21:58
 */
class MotanServiceExportListener implements InjectionListener {

    private final Class<?> clazz;
    private final MotanFactory motanFactory;
    private final Environment environment;

    public MotanServiceExportListener(Class<?> clazz, MotanFactory motanFactory, Environment environment) {
        this.clazz = clazz;
        this.motanFactory = motanFactory;
        this.environment = environment;
    }

    @Override
    public void afterInjection(Object injectee) {
        final MotanService service = clazz.getAnnotation(MotanService.class);
        String id = service.id();

        if (Strings.isNullOrEmpty(id)) {
            id = clazz.getName();
        }

        ServiceConfig serviceConfig = motanFactory.getServicesConfig().get(id);

        if (serviceConfig == null) {
            serviceConfig = new ServiceConfig();
        }

        serviceConfig.setApplication(environment.getName());

        serviceConfig.setRef(injectee);

        if (void.class.equals(service.interfaceClass())) {
            if (clazz.getInterfaces().length > 0) {
                Class<Object> clz = (Class<Object>) clazz.getInterfaces()[0];
                serviceConfig.setInterface(clz);
            } else {
                throw new IllegalStateException("Failed to export remote service class " + clazz.getName()
                    + ", cause: The @Service undefined interfaceClass or interfaceName, and the service class unimplemented any interfaces.");
            }
        } else {
            serviceConfig.setInterface(service.interfaceClass());
        }

        if (Strings.isNullOrEmpty(serviceConfig.getExport()) && !Strings.isNullOrEmpty(service.export())) {
            serviceConfig.setExport(service.export());
        }

        motanFactory.registerService(id, serviceConfig);

        serviceConfig.export();
    }


}
