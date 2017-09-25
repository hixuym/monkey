package io.sunflower.example.controllers;

import javax.inject.Inject;

import io.ebean.EbeanServer;
import io.sunflower.example.models.User;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.Results;

public class MainController {
    @Inject
    private EbeanServer ebeanServer;

    public Result index() {

        return Results.html();

    }

    public final Result helloWorldJson() {
        User u = new User();

        u.setName("michael");
        u.setAge(30);
        ebeanServer.save(u);

        SimplePojo simplePojo = new SimplePojo();
        simplePojo.content = "Hello World! Hello Json!" + u.getId();

        return Results.json().render(simplePojo);

    }

    public static class SimplePojo {

        public String content;

    }
}
