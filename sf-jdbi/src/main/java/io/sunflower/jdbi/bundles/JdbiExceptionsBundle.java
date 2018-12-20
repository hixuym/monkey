package io.sunflower.jdbi.bundles;

import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

/**
 * A bundle for logging {@link java.sql.SQLException}s and {@link org.jdbi.v3.core.JdbiException}s
 * so that their actual causes aren't overlooked.
 */
public class JdbiExceptionsBundle implements ConfiguredBundle<Configuration> {
    @Override
    public void run(Configuration configuration, Environment environment) {
//        environment.jersey().register(new LoggingSQLExceptionMapper());
//        environment.jersey().register(new LoggingJdbiExceptionMapper());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
