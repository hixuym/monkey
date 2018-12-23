package io.sunflower.quickstarters;

import io.sunflower.Application;
import io.sunflower.datasource.PooledDataSourceFactory;
import io.sunflower.orm.OrmBundle;
import io.sunflower.quickstarters.resources.HelloworldResource;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import org.conscrypt.OpenSSLProvider;

import java.security.Security;

/**
 * @author michael created on 17/9/2
 */
public class QuickstartersApplication extends Application<QuickstartersConfiguration> {

    static {
        Security.addProvider(new OpenSSLProvider());
    }

    public static void main(String[] args) throws Exception {
        new QuickstartersApplication().run(args);
    }

    @Override
    public String getName() {
        return "sf-quickstarters";
    }

    @Override
    public void initialize(Bootstrap<QuickstartersConfiguration> bootstrap) {
        bootstrap.addBundle(new OrmBundle<QuickstartersConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(QuickstartersConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(QuickstartersConfiguration configuration, Environment environment) {

        environment.guice().register(HelloworldResource.class);
    }

}
