package io.sunflower.example;

import ch.qos.logback.classic.Level;
import io.sunflower.Application;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

/**
 * Created by michael on 17/9/2.
 */
public class ExampleApplication extends Application<ExampleConfiguration> {

    public static void main(String[] args) throws Exception {
        new ExampleApplication().run(args);
    }

    @Override
    protected Level bootstrapLogLevel() {
        return Level.DEBUG;
    }

    @Override
    public String getName() {
        return "sf-example";
    }

    @Override
    public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception {
    }

}
