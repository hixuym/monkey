package io.sunflower.quickstarters;

import io.sunflower.Application;
import io.sunflower.quickstarters.core.GreetingService;
import io.sunflower.quickstarters.core.GreetingServiceImpl;
import io.sunflower.quickstarters.resources.HelloworldResource;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;

/**
 * @author michael created on 17/9/2
 */
public class QuickstartersApplication extends Application<QuickstartersConfiguration> {

    public static void main(String[] args) throws Exception {
        new QuickstartersApplication().run(args);
    }

    @Override
    public String getName() {
        return "sf-quickstarters";
    }

    @Override
    public void initialize(Bootstrap<QuickstartersConfiguration> bootstrap) {
    }

    @Override
    public void run(QuickstartersConfiguration configuration, Environment environment) throws Exception {

        environment.guice().register(HelloworldResource.class);
    }

}
