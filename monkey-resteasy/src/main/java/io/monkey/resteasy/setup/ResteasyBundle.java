package io.monkey.resteasy.setup;

import com.google.common.base.Stopwatch;
import io.monkey.Configuration;
import io.monkey.ConfiguredBundle;
import io.monkey.resteasy.ResteasyConfiguration;
import io.monkey.resteasy.ResteasyFactory;
import io.monkey.resteasy.validation.Validators;
import io.monkey.setup.Bootstrap;
import io.monkey.setup.Environment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResteasyBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static final Logger logger = LoggerFactory.getLogger(ResteasyBundle.class);

    private final ResteasyConfiguration<T> resteasyConfiguration;
    private final ResteasyDeployment deployment = new ResteasyDeployment();

    public ResteasyBundle(ResteasyConfiguration<T> resteasyConfiguration) {
        this.resteasyConfiguration = resteasyConfiguration;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.setValidatorFactory(Validators.newValidatorFactory());
    }

    @Override
    public void run(T configuration, Environment environment) {
        Stopwatch sw = Stopwatch.createStarted();

        ResteasyFactory resteasyFactory = resteasyConfiguration.getResteasyFactory(configuration);

        DeploymentInfo deploymentInfo = resteasyFactory.build(deployment);

        ServletContainer container = ServletContainer.Factory.newInstance();

        final DeploymentManager manager = container.addDeployment(deploymentInfo);

        final String contextPath = deploymentInfo.getContextPath();

        environment.guicify().register(new ResteasyModule(manager, contextPath), new RequestScopeModule());

        environment.lifecycle().manage(new ResteasyManager(environment, deployment, contextPath));

        logger.info("ResteasyBundle initialized {}.", sw);
    }


}
