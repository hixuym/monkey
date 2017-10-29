package io.sunflower.metrics;

import com.google.common.collect.ImmutableSet;

interface StringMatchingStrategy {

    /**
     * matching strategy
     * @param matchExpressions
     * @param metricName
     * @return
     */
    boolean containsMatch(ImmutableSet<String> matchExpressions, String metricName);
}
