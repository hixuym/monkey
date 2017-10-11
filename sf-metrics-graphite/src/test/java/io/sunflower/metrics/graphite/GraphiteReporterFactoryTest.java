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

package io.sunflower.metrics.graphite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteUDP;
import io.sunflower.configuration.YamlConfigurationFactory;
import io.sunflower.jackson.DiscoverableSubtypeResolver;
import io.sunflower.jackson.Jackson;
import io.sunflower.validation.BaseValidator;
import java.util.Optional;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class GraphiteReporterFactoryTest {

  private final GraphiteReporter.Builder builderSpy = mock(GraphiteReporter.Builder.class);

  private GraphiteReporterFactory graphiteReporterFactory = new GraphiteReporterFactory() {
    @Override
    protected GraphiteReporter.Builder builder(MetricRegistry registry) {
      return builderSpy;
    }
  };

  @Test
  public void isDiscoverable() throws Exception {
    assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes())
        .contains(GraphiteReporterFactory.class);
  }

  @Test
  public void createDefaultFactory() throws Exception {
    final GraphiteReporterFactory factory = new YamlConfigurationFactory<>(
        GraphiteReporterFactory.class,
        BaseValidator.newValidator(), Jackson.newObjectMapper(), "sf")
        .build();
    assertThat(factory.getFrequency()).isEqualTo(Optional.empty());
  }

  @Test
  public void testNoAddressResolutionForGraphite() throws Exception {
    graphiteReporterFactory.build(new MetricRegistry());

    final ArgumentCaptor<Graphite> argument = ArgumentCaptor.forClass(Graphite.class);
    verify(builderSpy).build(argument.capture());

    final Graphite graphite = argument.getValue();
    assertThat(getField(graphite, "hostname")).isEqualTo("localhost");
    assertThat(getField(graphite, "port")).isEqualTo(2003);
    assertThat(getField(graphite, "address")).isNull();
  }

  @Test
  public void testCorrectTransportForGraphiteUDP() throws Exception {
    graphiteReporterFactory.setTransport("udp");
    graphiteReporterFactory.build(new MetricRegistry());

    final ArgumentCaptor<GraphiteUDP> argument = ArgumentCaptor.forClass(GraphiteUDP.class);
    verify(builderSpy).build(argument.capture());

    final GraphiteUDP graphite = argument.getValue();
    assertThat(getField(graphite, "hostname")).isEqualTo("localhost");
    assertThat(getField(graphite, "port")).isEqualTo(2003);
    assertThat(getField(graphite, "address")).isNull();
  }

  private static Object getField(GraphiteUDP graphite, String name) {
    try {
      return FieldUtils.getDeclaredField(GraphiteUDP.class, name, true).get(graphite);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  private static Object getField(Graphite graphite, String name) {
    try {
      return FieldUtils.getDeclaredField(Graphite.class, name, true).get(graphite);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
}
