package io.sunflower.gizmo;

import io.sunflower.Configuration;

public interface GizmoConfigurationFactory<T extends Configuration> {
    GizmoConfiguration getGizmoConfiguration(T configuration);
}
