package io.sunflower.example;

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
    public String getName() {
        return "sf-example";
    }

    @Override
    public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception {
        System.out.println(String.format(configuration.getTemplate(), configuration.getDefaultName()));
    }
}
