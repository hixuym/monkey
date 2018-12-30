package io.monkey.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.monkey.json.Jackson;
import io.monkey.validation.BaseValidator;
import org.junit.Test;

import javax.validation.Validator;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class ConfigurationFactoryFactoryTest {

    private final ConfigurationFactoryFactory<BaseConfigurationFactoryTest.Example> factoryFactory = new DefaultConfigurationFactoryFactory<>();
    private final Validator validator = BaseValidator.newValidator();

    @Test
    public void createDefaultFactory() throws Exception {
        File validFile = new File(Resources.getResource("factory-test-valid.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory =
            factoryFactory.create(BaseConfigurationFactoryTest.Example.class, validator, Jackson.newObjectMapper(), "mk");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getName())
            .isEqualTo("Coda Hale");
    }

    @Test
    public void createDefaultFactoryFailsUnknownProperty() throws Exception {
        File validFileWithUnknownProp = new File(
            Resources.getResource("factory-test-unknown-property.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory =
            factoryFactory.create(BaseConfigurationFactoryTest.Example.class, validator, Jackson.newObjectMapper(), "mk");

        assertThatExceptionOfType(ConfigurationException.class)
            .isThrownBy(() -> factory.build(validFileWithUnknownProp))
            .withMessageContaining("Unrecognized field at: trait");
    }

    @Test
    public void createFactoryAllowingUnknownProperties() throws Exception {
        ConfigurationFactoryFactory<BaseConfigurationFactoryTest.Example> customFactory = new PassThroughConfigurationFactoryFactory();
        File validFileWithUnknownProp = new File(
            Resources.getResource("factory-test-unknown-property.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory =
            customFactory.create(
                BaseConfigurationFactoryTest.Example.class,
                validator,
                Jackson.newObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES),
                "mk");
        BaseConfigurationFactoryTest.Example example = factory.build(validFileWithUnknownProp);
        assertThat(example.getName())
            .isEqualTo("Mighty Wizard");
    }

    private static final class PassThroughConfigurationFactoryFactory
            extends DefaultConfigurationFactoryFactory<BaseConfigurationFactoryTest.Example> {
        @Override
        protected ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
            return objectMapper;
        }
    }
}
