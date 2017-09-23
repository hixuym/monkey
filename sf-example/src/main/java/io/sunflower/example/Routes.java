package io.sunflower.example;

import io.sunflower.example.controllers.MainController;
import io.sunflower.gizmo.AssetsController;
import io.sunflower.gizmo.Router;
import io.sunflower.gizmo.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    @Override
    public void init(Router router) {

        router.GET().route("/").with(MainController::index);
        router.GET().route("/hello_world.json").with(MainController::helloWorldJson);

        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController::serveWebJars);
        router.GET().route("/assets/{fileName: .*}").with(AssetsController::serveStatic);

        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/.*").with(MainController::index);
    }
}
