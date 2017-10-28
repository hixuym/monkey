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

package io.sunflower.guice.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author ra
 */
class TimedInterceptor implements MethodInterceptor {

    private final Provider<MetricRegistry> metricRegistry;

    @Inject
    public TimedInterceptor(Provider<MetricRegistry> metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        String timerName = invocation.getMethod().getAnnotation(Timed.class).value();

        if (timerName.isEmpty()) {
            timerName = MetricRegistry
                    .name(invocation.getThis().getClass().getSuperclass(), invocation.getMethod().getName());
        }

        Timer.Context timerContext
                = metricRegistry.get()
                .timer(timerName)
                .time();

        try {
            return invocation.proceed();
        } finally {
            timerContext.stop();
            timerContext.close();
        }
    }

}
