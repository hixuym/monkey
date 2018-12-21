package io.sunflower.quickstarters;

import io.ebean.config.ServerConfig;
import io.sunflower.Application;
import io.sunflower.db.PooledDataSourceFactory;
import io.sunflower.ebean.EbeanBundle;
import io.sunflower.quickstarters.core.User;
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
        bootstrap.addBundle(new EbeanBundle<QuickstartersConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(QuickstartersConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            protected void configure(ServerConfig serverConfig) {
//                serverConfig.addClass(User.class);
            }
        });
    }

    @Override
    public void run(QuickstartersConfiguration configuration, Environment environment) {

        environment.guice().register(HelloworldResource.class);
    }

}
