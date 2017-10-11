package io.sunflower.gizmo.utils;

public class ClockImpl implements Clock {

  @Override
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }

}
