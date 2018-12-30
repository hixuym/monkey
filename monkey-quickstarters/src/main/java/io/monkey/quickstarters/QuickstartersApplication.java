package io.monkey.quickstarters;

import io.monkey.Application;
import io.monkey.datasource.PooledDataSourceFactory;
import io.monkey.jaxrs.auth.basic.BasicAuthModule;
import io.monkey.jaxrs.setup.JaxrsBundle;
import io.monkey.jaxrs.setup.JaxrsDeploymentFactory;
import io.monkey.orm.OrmBundle;
import io.monkey.quickstarters.resources.HelloworldResource;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
import io.monkey.setup.GuicifyEnvironment;
//import org.conscrypt.OpenSSLProvider;
//
//import java.security.Security;

/**
 * @author michael created on 17/9/2
 */
public class QuickstartersApplication extends Application<QuickstartersConfiguration> {

//    static {
//        Security.addProvider(new OpenSSLProvider());
//
//    }

    public static void main(String[] args) throws Exception {
        new QuickstartersApplication().run(args);
    }

    @Override
    public String getName() {
        return "mk-quickstarters";
    }

    @Override
    public void initialize(Bootstrap<QuickstartersConfiguration> bootstrap) {
        bootstrap.addBundle(new OrmBundle<QuickstartersConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(QuickstartersConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new JaxrsBundle<QuickstartersConfiguration>() {

            @Override
            public JaxrsDeploymentFactory build(QuickstartersConfiguration configuration) {
                return configuration.getJaxrsDeploymentFactory();
            }

            @Override
            protected void bindResources(GuicifyEnvironment facotry) {
                facotry.register(HelloworldResource.class);
            }
        });
    }

    @Override
    public void run(QuickstartersConfiguration configuration, Environment environment) {
        environment.guicify().register(new BasicAuthModule() {
            @Override
            protected void configureAuthenticator() {
                
            }
        });
    }

}
