package io.sunflower.gizmo;

/**
 * Created by michael on 17/9/4.
 */
public interface ControllerMethodInvoker {

    /**
     *
     * @param controller instance
     * @param context request context
     * @return
     */
    Result invoke(Object controller, Context context);

}
