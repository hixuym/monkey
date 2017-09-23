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

package io.sunflower.db;

import com.codahale.metrics.health.HealthCheck;
import io.sunflower.util.Duration;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimeBoundHealthCheckTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testCheck() throws InterruptedException, ExecutionException, TimeoutException {
        final ExecutorService executorService = mock(ExecutorService.class);
        final Duration duration = mock(Duration.class);
        when(duration.getQuantity()).thenReturn(5L);
        when(duration.getUnit()).thenReturn(TimeUnit.SECONDS);

        final Callable<HealthCheck.Result> callable = mock(Callable.class);
        final Future<HealthCheck.Result> future = mock(Future.class);
        when(executorService.submit(callable)).thenReturn(future);

        new TimeBoundHealthCheck(executorService, duration).check(callable);
        verify(executorService, times(1)).submit(callable);
        verify(future, times(1)).get(duration.getQuantity(), duration.getUnit());
    }
}
