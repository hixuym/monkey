package io.monkey.jaxrs.setup;

import io.monkey.Configuration;

public interface JaxrsConfiguration<T extends Configuration> {
    JaxrsDeploymentFactory build(T configuration);
}
