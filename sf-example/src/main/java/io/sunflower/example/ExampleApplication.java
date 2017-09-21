package io.sunflower.example;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.OptionalBinder;

import ch.qos.logback.classic.Level;
import io.sunflower.Application;
import io.sunflower.gizmo.Gizmo;
import io.sunflower.gizmo.GizmoBundle;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.InstrumentedGizmo;
import io.sunflower.gizmo.application.ApplicationRoutes;
import io.sunflower.gizmo.server.ServerCommand;
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
        bootstrap.addCommand(new ServerCommand<>(this));

        bootstrap.addBundle(new GizmoBundle<ExampleConfiguration>() {
            @Override
            public GizmoConfiguration getGizmoConfiguration(ExampleConfiguration configuration) {
                return configuration.getGizmoConfiguration();
            }
        });
    }

    @Override
    public void run(ExampleConfiguration configuration, Environment environment) throws Exception {

        environment.guicey().addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ApplicationRoutes.class).to(Routes.class);
                OptionalBinder.newOptionalBinder(binder(), Gizmo.class)
                    .setBinding().to(InstrumentedGizmo.class);
            }
        });
    }

}
