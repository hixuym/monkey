package io.sunflower.undertow;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sunflower.jackson.Discoverable;
import io.sunflower.setup.Environment;
import io.undertow.Undertow;

/**
 * A factory for creating Undertow {@see Undertow.ListenerBuilder}s.
 * @author michael
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ConnectorFactory extends Discoverable {

    /**
     * Create a new connector.
     *
     * @return a {@see Undertow.ListenerBuilder}
     */
    Undertow.ListenerBuilder build(Environment environment);
}
