package io.sunflower.gizmo;

import com.google.inject.Injector;

import java.lang.reflect.Method;

/**
 * Created by michael on 17/9/4.
 */
public interface ControllerMethodInvokerFactory {
    ControllerMethodInvoker build(
        Method functionalMethod,
        Method implementationMethod,
        Injector injector);
}
