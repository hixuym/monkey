package io.sunflower.jaxrs.setup;

import com.google.inject.Binding;
import com.google.inject.Injector;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.SunflowerException;
import io.sunflower.http.server.ApplicationHandlerRegister;
import io.sunflower.jaxrs.validation.Validators;
import io.sunflower.server.ServerFactory;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.sunflower.setup.InjectorFacotry;
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
        bootstrap.injectorFacotry().register(new JaxrsModule(), new RequestScopeModule());

        bindResources(bootstrap.injectorFacotry());
    }

    protected void bindResources(InjectorFacotry facotry) {

    }

    @Override
    public void run(T configuration, Environment environment) {

        JaxrsDeploymentFactory jaxrsDeploymentFactory = build(configuration);

        ApplicationHandlerRegister register = environment.getInjector().getInstance(ApplicationHandlerRegister.class);

        DeploymentInfo deploymentInfo = jaxrsDeploymentFactory.build(deployment);

        ServletContainer container = ServletContainer.Factory.newInstance();
        DeploymentManager manager = container.addDeployment(deploymentInfo);
        manager.deploy();

        String contextPath = deploymentInfo.getContextPath();

        try {
            register.registry(contextPath, manager.start());
        } catch (ServletException e) {
            throw new SunflowerException(e);
        }

        environment.addServerLifecycleListener((server -> {
            scanResources(environment.getInjector());
            ServerFactory serverFactory = configuration.getServerFactory();

            logger.info("JAX-RS WADL at: {}", serverFactory.getSchema() + "://"
                    + (serverFactory.getHost() == null ? "localhost" : serverFactory.getHost())
                    + ":" + serverFactory.getPort()
                    + (contextPath.endsWith("/") ? contextPath : contextPath + "/")
                    + "application.xml");
        }));
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
            final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
            logger.info("registering factory for {}", beanClass.getName());
            deployment.getRegistry().addResourceFactory(resourceFactory);

        }
    }

}
