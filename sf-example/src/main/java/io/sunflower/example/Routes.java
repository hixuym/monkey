package io.sunflower.example;

import io.sunflower.example.controllers.MainController;
import io.sunflower.gizmo.Router;
import io.sunflower.gizmo.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    @Override
    public void init(Router router) {
        router.GET().route("/hello/index").with(MainController::index);

        router.GET().route("/*").with(MainController::index);
    }
}
