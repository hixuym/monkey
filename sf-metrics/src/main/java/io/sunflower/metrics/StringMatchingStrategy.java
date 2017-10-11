package io.sunflower.metrics;

import com.google.common.collect.ImmutableSet;

interface StringMatchingStrategy {

  boolean containsMatch(ImmutableSet<String> matchExpressions, String metricName);
}
