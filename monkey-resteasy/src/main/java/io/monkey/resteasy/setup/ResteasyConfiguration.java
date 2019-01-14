package io.monkey.resteasy.setup;

import io.monkey.Configuration;

public interface ResteasyConfiguration<T extends Configuration> {
    ResteasyFactory getResteasyFactory(T configuration);
}
