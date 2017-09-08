package io.sunflower.gizmo;

import io.sunflower.Configuration;

/**
 * Created by michael on 17/9/4.
 */
public interface GizmoConfiguration<T extends Configuration> {
    GizmoFactory build(T configuration);
}
