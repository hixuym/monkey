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

package io.monkey.metrics;
import com.codahale.metrics.ScheduledReporter;
import io.monkey.util.Duration;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

public class ScheduledReporterManagerTest {

    @Test
    public void testStopWithoutReporting() throws Exception {
        final boolean reportOnStop = false;
        ScheduledReporter mockReporter = Mockito.mock(ScheduledReporter.class);
        ScheduledReporterManager manager = new ScheduledReporterManager(mockReporter, Duration.minutes(5), reportOnStop);

        manager.start();
        manager.stop();

        Mockito.verify(mockReporter).start(Mockito.eq(5L), Mockito.eq(TimeUnit.MINUTES));
        Mockito.verify(mockReporter).stop();
        Mockito.verifyNoMoreInteractions(mockReporter);
    }

    @Test
    public void testStopWithReporting() throws Exception {
        final boolean reportOnStop = true;
        ScheduledReporter mockReporter = Mockito.mock(ScheduledReporter.class);
        ScheduledReporterManager manager = new ScheduledReporterManager(mockReporter, Duration.minutes(5), reportOnStop);

        manager.start();
        manager.stop();

        Mockito.verify(mockReporter).start(Mockito.eq(5L), Mockito.eq(TimeUnit.MINUTES));
        Mockito.verify(mockReporter).report();
        Mockito.verify(mockReporter).stop();
        Mockito.verifyNoMoreInteractions(mockReporter);
    }

}
