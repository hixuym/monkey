package io.sunflower.configuration;

import com.google.common.io.Resources;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import javax.validation.Validator;

import io.sunflower.jackson.Jackson;
import io.sunflower.validation.BaseValidator;

import static org.assertj.core.api.Assertions.assertThat;


public class ConfigurationFactoryFactoryTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final ConfigurationFactoryFactory<BaseConfigurationFactoryTest.Example> factoryFactory = new DefaultConfigurationFactoryFactory<>();
    private final Validator validator = BaseValidator.newValidator();

    @Test
    public void createDefaultFactory() throws Exception {
        File validFile = new File(Resources.getResource("factory-test-valid.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory =
            factoryFactory.create(BaseConfigurationFactoryTest.Example.class, validator, Jackson.newObjectMapper(), "sf");
        final BaseConfigurationFactoryTest.Example example = factory.build(validFile);
        assertThat(example.getName())
            .isEqualTo("Coda Hale");
    }

    @Test
    public void createDefaultFactoryFailsUnknownProperty() throws Exception {
        File validFileWithUnknownProp = new File(
            Resources.getResource("factory-test-unknown-property.yml").toURI());
        ConfigurationFactory<BaseConfigurationFactoryTest.Example> factory =
            factoryFactory.create(BaseConfigurationFactoryTest.Example.class, validator, Jackson.newObjectMapper(), "sf");
        expectedException.expect(ConfigurationException.class);
        expectedException.expectMessage("Unrecognized field at: trait");
        factory.build(validFileWithUnknownProp);
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
                "sf");
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
