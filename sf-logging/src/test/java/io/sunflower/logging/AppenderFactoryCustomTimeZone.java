package io.sunflower.logging;

import com.google.common.io.Resources;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.TimeZone;

import io.sunflower.configuration.YamlConfigurationFactory;
import io.sunflower.jackson.Jackson;
import io.sunflower.validation.BaseValidator;

import static org.assertj.core.api.Assertions.assertThat;

public class AppenderFactoryCustomTimeZone {

    static {
        BootstrapLogging.bootstrap();
    }

    private final YamlConfigurationFactory<ConsoleAppenderFactory> factory = new YamlConfigurationFactory<>(
        ConsoleAppenderFactory.class, BaseValidator.newValidator(), Jackson.newObjectMapper(), "dw");

    private static File loadResource(String resourceName) throws URISyntaxException {
        return new File(Resources.getResource(resourceName).toURI());
    }

    @Test
    public void testLoadAppenderWithTimeZoneInFullFormat() throws Exception {
        final ConsoleAppenderFactory appender = factory.build(loadResource("yaml/appender_with_time_zone_in_full_format.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("America/Los_Angeles");
    }

    @Test
    public void testLoadAppenderWithTimeZoneInCustomFormat() throws Exception {
        final ConsoleAppenderFactory appender = factory.build(loadResource("yaml/appender_with_custom_time_zone_format.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("GMT-02:00");
    }

    @Test
    public void testLoadAppenderWithNoTimeZone() throws Exception {
        final ConsoleAppenderFactory appender = factory.build(loadResource("yaml/appender_with_no_time_zone.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("UTC");
    }

    @Test
    public void testLoadAppenderWithUtcTimeZone() throws Exception {
        final ConsoleAppenderFactory appender = factory.build(loadResource("yaml/appender_with_utc_time_zone.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("UTC");
    }

    @Test
    public void testLoadAppenderWithWrongTimeZone() throws Exception {
        final ConsoleAppenderFactory appender = factory.build(loadResource("yaml/appender_with_wrong_time_zone.yml"));
        assertThat(appender.getTimeZone().getID()).isEqualTo("GMT");
    }

    @Test
    public void testLoadAppenderWithSystemTimeZone() throws Exception {
        final ConsoleAppenderFactory appender = factory.build(loadResource("yaml/appender_with_system_time_zone.yml"));
        assertThat(appender.getTimeZone()).isEqualTo(TimeZone.getDefault());
    }

}
