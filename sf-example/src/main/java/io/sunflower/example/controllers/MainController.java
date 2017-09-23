package io.sunflower.example.controllers;

import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.Results;

public class MainController {
    public Result index() {

        return Results.html();

    }

    public final Result helloWorldJson() {

        SimplePojo simplePojo = new SimplePojo();
        simplePojo.content = "Hello World! Hello Json!";

        return Results.json().render(simplePojo);

    }

    public static class SimplePojo {

        public String content;

    }
}
