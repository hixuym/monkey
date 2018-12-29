package io.sunflower.jaxrs.setup;

import com.google.common.base.Stopwatch;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.SunflowerException;
import io.sunflower.jaxrs.validation.Validators;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.sunflower.setup.GuicifyEnvironment;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class JaxrsBundle<T extends Configuration> implements ConfiguredBundle<T>, JaxrsConfiguration<T> {

    private static final Logger logger = LoggerFactory.getLogger(JaxrsBundle.class);

    private ResteasyDeployment deployment = new ResteasyDeployment();

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

        bootstrap.setValidatorFactory(Validators.newValidatorFactory());

    }

    protected void bindResources(GuicifyEnvironment facotry) {

    }

    @Override
    public void run(T configuration, Environment environment) {
        Stopwatch sw = Stopwatch.createStarted();

        JaxrsDeploymentFactory jaxrsDeploymentFactory = build(configuration);
        DeploymentInfo deploymentInfo = jaxrsDeploymentFactory.build(deployment);
        ServletContainer container = ServletContainer.Factory.newInstance();
        final DeploymentManager manager = container.addDeployment(deploymentInfo);

        final String contextPath = deploymentInfo.getContextPath();

        environment.guicify().register(new JaxrsModule(), new RequestScopeModule());

        environment.guicify().register(new AbstractModule() {
            @Override
            protected void configure() {
                MapBinder<String, HttpHandler> mapBinder = MapBinder.newMapBinder(binder(), String.class, HttpHandler.class);
                mapBinder.addBinding(contextPath).toProvider(new JaxrsHandlerProvider(manager));
            }
        });

        bindResources(environment.guicify());

        environment.addServerLifecycleListener((server -> {
            scanResources(environment.getInjector());

            logger.info("JAX-RS WADL at: {}", (contextPath.endsWith("/") ? contextPath : contextPath + "/") + "application.xml");
        }));

        logger.info("JaxrsBundle initialized {}.", sw);
    }

    private static class JaxrsHandlerProvider implements javax.inject.Provider<HttpHandler> {

        private DeploymentManager manager;

        public JaxrsHandlerProvider(DeploymentManager manager) {
            this.manager = manager;
        }

        @Override
        public HttpHandler get() {
            try {
                manager.deploy();
                return manager.start();
            } catch (ServletException e) {
                throw new SunflowerException(e);
            }
        }
    }

    private void scanResources(Injector injector) {
        List<Binding<?>> rootResourceBindings = new ArrayList<>();
        for (final Binding<?> binding : injector.getBindings().values()) {
            final Type type = binding.getKey().getTypeLiteral().getRawType();
            if (type instanceof Class) {
                final Class<?> beanClass = (Class) type;
                if (GetRestful.isRootResource(beanClass)) {
                    // deferred registration
                    rootResourceBindings.add(binding);
                }

                if (beanClass.isAnnotationPresent(Provider.class)) {
                    logger.info("registering provider instance for {}", beanClass.getName());
                    deployment.getProviderFactory().registerProviderInstance(binding.getProvider().get());
                }

            }
        }

        for (Binding<?> binding : rootResourceBindings) {

            Class<?> beanClass = (Class) binding.getKey().getTypeLiteral().getType();

            if (Scopes.isSingleton(binding)) {
                logger.info("registering singleton factory for {}, make sure threadsafe.", beanClass.getName());
                deployment.getRegistry().addSingletonResource(binding.getProvider().get());
            } else {
                final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
                logger.info("registering factory for {}", beanClass.getName());
                deployment.getRegistry().addResourceFactory(resourceFactory);
            }

        }
    }

}
