package io.monkey.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import javax.annotation.Nullable;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * A utility class for Jackson.
 */
public class Jackson {
    private Jackson() { /* singleton */ }

    /**
     * Creates a new {@link ObjectMapper} with Guava, Logback, and Joda Time support, as well as
     * support for {@link JsonSnakeCase}. Also includes all {@link Discoverable} interface implementations.
     */
    public static ObjectMapper newObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();

        return configure(mapper);
    }

    /**
     * Creates a new {@link ObjectMapper} with a custom {@link JsonFactory}
     * with Guava, Logback, and Joda Time support, as well as support for {@link JsonSnakeCase}.
     * Also includes all {@link Discoverable} interface implementations.
     *
     * @param jsonFactory instance of {@link JsonFactory} to use
     *                    for the created {@link ObjectMapper} instance.
     */
    public static ObjectMapper newObjectMapper(@Nullable JsonFactory jsonFactory) {
        final ObjectMapper mapper = new ObjectMapper(jsonFactory);

        return configure(mapper);
    }

    /**
     * Creates a new minimal {@link ObjectMapper} that will work with Dropwizard out of box.
     * <p><b>NOTE:</b> Use it, if the default Dropwizard's {@link ObjectMapper}, created in
     * {@link #newObjectMapper()}, is too aggressive for you.</p>
     */
    public static ObjectMapper newMinimalObjectMapper() {
        return new ObjectMapper()
                .registerModule(new GuavaModule())
                .setSubtypeResolver(new DiscoverableSubtypeResolver())
                .disable(FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private static ObjectMapper configure(ObjectMapper mapper) {
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new GuavaExtrasModule());
        mapper.registerModule(new AfterburnerModule());
        mapper.registerModule(new FuzzyEnumModule());
        mapper.registerModule(new ParameterNamesModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());// use jsr310 instead of joda
        mapper.registerModule(new SafeJavaTimeModule());
        mapper.setPropertyNamingStrategy(new AnnotationSensitivePropertyNamingStrategy());
        mapper.setSubtypeResolver(new DiscoverableSubtypeResolver());
        mapper.disable(FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }
}