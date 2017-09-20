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

package io.sunflower.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author James Moger
 */
class CountedInterceptor implements MethodInterceptor {

    private final Provider<MetricRegistry> metricRegistry;

    @Inject
    public CountedInterceptor(Provider<MetricRegistry> metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Counted counted = invocation.getMethod().getAnnotation(Counted.class);
        String counterName = counted.value();

        if (counterName.isEmpty()) {
            counterName = MetricRegistry.name(invocation.getThis().getClass()
                .getSuperclass(), invocation.getMethod().getName());
        }

        Counter counter = metricRegistry.get().counter(counterName);

        counter.inc();

        try {
            return invocation.proceed();
        } finally {
            if (counted.active()) {
                counter.dec();
            }
        }
    }

}
