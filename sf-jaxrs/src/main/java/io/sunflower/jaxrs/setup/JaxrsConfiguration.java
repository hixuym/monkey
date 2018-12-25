package io.sunflower.jaxrs.setup;

import io.sunflower.Configuration;

public interface JaxrsConfiguration<T extends Configuration> {
    JaxrsDeploymentFactory build(T configuration);
}
