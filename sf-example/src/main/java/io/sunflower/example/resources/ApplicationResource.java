package io.sunflower.example.resources;

import io.ebean.EbeanServer;
import io.sunflower.ebean.Transactional;
import io.sunflower.example.core.User;
import io.sunflower.example.core.UserMapper;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.Results;
import javax.inject.Inject;

public class ApplicationResource {

  @Inject
  private EbeanServer ebeanServer;

  @Inject
  private UserMapper userMapper;

  public Result index() {
    return Results.html();
  }

  @io.sunflower.mybatis.Transactional
  public Result userCount() {
    return Results.json().render(userMapper.getUserCount());
  }

  @Transactional
  public Result helloWorldJson() {
    User u = new User();

    u.setName("michael");
    u.setAge(30);
    ebeanServer.save(u);

    SimplePojo simplePojo = new SimplePojo();
    simplePojo.content = "Hello World! Hello Json!";

    return Results.json().render(simplePojo);

  }

  public static class SimplePojo {

    public String content;

  }
}
