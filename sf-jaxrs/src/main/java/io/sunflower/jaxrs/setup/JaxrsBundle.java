package io.sunflower.jaxrs.setup;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Injector;
import io.sunflower.Configuration;
import io.sunflower.ConfiguredBundle;
import io.sunflower.SunflowerException;
import io.sunflower.jaxrs.validation.Validators;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
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
import java.util.Map;

import static io.undertow.servlet.Servlets.servlet;

public class JaxrsBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static final Logger logger = LoggerFactory.getLogger(JaxrsBundle.class);

    private String contextPath;
    private Map<String, String> contextParams;
    private Map<String, String> initParams;

    private final ServletContainer container = ServletContainer.Factory.newInstance();
    private final ResteasyDeployment deployment = new ResteasyDeployment();
    private final static String SERVLET_NAME = "ResteasyServlet";

    private final PathHandler root = new PathHandler();

    public JaxrsBundle(String contextPath,
                       Map<String, String> contextParams,
                       Map<String, String> initParams) {
        this.contextPath = contextPath;
        this.contextParams = contextParams;
        this.initParams = initParams;
    }

    public JaxrsBundle() {
        this("/", null, null);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        deploy();
        bootstrap.setValidatorFactory(Validators.newValidatorFactory());
        bootstrap.injectorFacotry().register(new JaxrsModule(), new RequestScopeModule());
        bootstrap.injectorFacotry().register(new AbstractModule() {
            @Override
            protected void configure() {
                bind(HttpHandler.class).toInstance(root);
            }
        });
    }

    @Override
    public void run(T configuration, Environment environment) {
        environment.addServerLifecycleListener((server -> scanResources(environment.getInjector())));
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

    private void deploy() {
        if (contextPath == null) contextPath = "/";
        if (!contextPath.startsWith("/")) contextPath = "/" + contextPath;
        DeploymentInfo deploymentInfo = asUndertowDeployment(deployment);
        deploymentInfo.setContextPath(this.contextPath);
        deploymentInfo.setDeploymentName("Resteasy" + contextPath);
        deploymentInfo.setClassLoader(deployment.getClass().getClassLoader());

        if (contextParams != null) {
            for (Map.Entry<String, String> e : contextParams.entrySet()) {
                deploymentInfo.addInitParameter(e.getKey(), e.getValue());
            }
        }
        if (initParams != null) {
            ServletInfo servletInfo = deploymentInfo.getServlets().get(SERVLET_NAME);
            for (Map.Entry<String, String> e : initParams.entrySet()) {
                servletInfo.addInitParam(e.getKey(), e.getValue());
            }
        }

        DeploymentManager manager = container.addDeployment(deploymentInfo);
        manager.deploy();
        try {
            root.addPrefixPath(deploymentInfo.getContextPath(), manager.start());
        } catch (ServletException e) {
            throw new SunflowerException(e);
        }
    }

    private DeploymentInfo asUndertowDeployment(ResteasyDeployment deployment) {
        return asUndertowDeployment(deployment, "/");
    }

    private DeploymentInfo asUndertowDeployment(ResteasyDeployment deployment, String mapping) {
        if (mapping == null) mapping = "/";
        if (!mapping.startsWith("/")) mapping = "/" + mapping;
        if (!mapping.endsWith("/")) mapping += "/";
        mapping = mapping + "*";
        String prefix = null;
        if (!mapping.equals("/*")) prefix = mapping.substring(0, mapping.length() - 2);
        ServletInfo resteasyServlet = servlet(SERVLET_NAME, HttpServlet30Dispatcher.class)
                .setAsyncSupported(true)
                .setLoadOnStartup(1)
                .addMapping(mapping);
        if (prefix != null) resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", prefix);

        return new DeploymentInfo()
                .addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
                .addServlet(resteasyServlet);
    }

    /**
     * Maps a path prefix to a resource handler to allow serving resources other than the JAX-RS endpoints.
     * For example, this can be used for serving static resources like web pages or API documentation that might
     * be deployed with the REST application server.
     *
     * @param path
     * @param handler
     */
    protected void addResourcePrefixPath(String path, ResourceHandler handler) {
        root.addPrefixPath(path, handler);
    }
}
