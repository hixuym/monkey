package io.sunflower.gizmo;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;

import javax.inject.Singleton;

import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.gizmo.bodyparser.BodyParserEngineJson;
import io.sunflower.gizmo.bodyparser.BodyParserEnginePost;
import io.sunflower.gizmo.bodyparser.BodyParserEngineXml;
import io.sunflower.gizmo.params.ParamParser;
import io.sunflower.gizmo.server.GizmoServerConfigurable;
import io.sunflower.gizmo.server.GizmoServerFactory;
import io.sunflower.gizmo.server.ServerCommand;
import io.sunflower.gizmo.server.UndertowContext;
import io.sunflower.gizmo.template.TemplateEngineJson;
import io.sunflower.gizmo.template.TemplateEngineJsonP;
import io.sunflower.gizmo.template.TemplateEngineText;
import io.sunflower.gizmo.template.TemplateEngineXml;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.sunflower.undertow.handler.GarbageCollectionTask;
import io.sunflower.undertow.handler.LogConfigurationTask;
import io.sunflower.undertow.handler.TaskManager;

public abstract class GizmoBundle<T extends Configuration> implements ConfiguredBundle<T>, GizmoServerConfigurable<T> {

    private volatile GizmoServerFactory serverFactory;

    @Override
    public void run(T configuration, Environment environment) throws Exception {

        this.serverFactory = getGizmoServerFacotory(configuration);

        environment.guicey().addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(GizmoConfiguration.class).toInstance(serverFactory.gizmoConfig());

                // Routing
                Multibinder.newSetBinder(binder(), ParamParser.class);
                bind(RouteBuilder.class).to(RouteBuilderImpl.class);
                bind(Router.class).to(RouterImpl.class).in(Singleton.class);

                bind(BodyParserEnginePost.class);
                bind(BodyParserEngineXml.class);
                bind(BodyParserEngineJson.class);

                bind(TemplateEngineJson.class);
                bind(TemplateEngineJsonP.class);
                bind(TemplateEngineXml.class);
                bind(TemplateEngineText.class);

                bind(Context.class).to(UndertowContext.class);

                OptionalBinder.newOptionalBinder(binder(), Gizmo.class)
                    .setDefault().to(GizmoDefault.class).in(Singleton.class);

                OptionalBinder.newOptionalBinder(binder(), ExceptionHandler.class)
                    .setDefault().to(DefaultExceptionHandler.class).in(Singleton.class);

                TaskManager taskManager = new TaskManager();

                taskManager.add(new GarbageCollectionTask());
                taskManager.add(new LogConfigurationTask());

                bind(TaskManager.class).toInstance(taskManager);
            }
        });
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.addCommand(new ServerCommand<>(bootstrap.getApplication(), this));
    }

    public GizmoServerFactory getServerFactory() {
        return serverFactory;
    }
}
