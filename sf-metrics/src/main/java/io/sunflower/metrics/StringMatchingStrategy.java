package io.sunflower.metrics;

import java.util.Set;

interface StringMatchingStrategy {
    boolean containsMatch(Set<String> matchExpressions, String metricName);
}
