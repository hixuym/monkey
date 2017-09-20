package io.sunflower.example.controllers;

import com.google.common.collect.ImmutableMap;

import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.Results;

public class MainController {

    public Result index() {

        return Results.json().render(ImmutableMap.of("michael", "how are you?"));

    }
}
