package io.sunflower.lifecycle;

import com.google.common.util.concurrent.Service;

/**
 * Created by michael on 17/9/1.
 */
public interface LifecycleListener {

    default void started() {}

    default void stopped() {}

    default void failured(Service service) {}
}
