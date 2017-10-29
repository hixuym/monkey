package io.sunflower.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.Validator;

/**
 * @author michael
 */
public interface ConfigurationFactoryFactory<T> {

    /**
     * get facotry
     * @param klass
     * @param validator
     * @param objectMapper
     * @param propertyPrefix
     * @return
     */
    ConfigurationFactory<T> create(Class<T> klass,
                                   Validator validator,
                                   ObjectMapper objectMapper,
                                   String propertyPrefix);
}
