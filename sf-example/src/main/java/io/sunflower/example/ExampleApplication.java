package io.sunflower.example;

import com.google.inject.AbstractModule;

import io.sunflower.Application;
import io.sunflower.db.PooledDataSourceFactory;
import io.sunflower.ebean.EbeanBundle;
import io.sunflower.example.api.Routes;
import io.sunflower.example.cli.DumpConfigCommand;
import io.sunflower.gizmo.Gizmo;
import io.sunflower.gizmo.InstrumentedGizmo;
import io.sunflower.gizmo.application.ApplicationRoutes;
import io.sunflower.gizmo.server.GizmoBundle;
import io.sunflower.gizmo.template.TemplateEngineFreemarker;
import io.sunflower.mybatis.MybatisBundle;
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
        bootstrap.addCommand(new DumpConfigCommand());
        bootstrap.addBundle(new GizmoBundle<>());
        bootstrap.addBundle(new EbeanBundle<ExampleConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(ExampleConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new MybatisBundle<ExampleConfiguration>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(ExampleConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception {
        environment.guicey().addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ApplicationRoutes.class).to(Routes.class);
                bind(TemplateEngineFreemarker.class);
                bind(Gizmo.class).to(InstrumentedGizmo.class);
            }
        });
    }

}
