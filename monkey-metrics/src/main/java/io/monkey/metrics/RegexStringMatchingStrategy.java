package io.monkey.metrics;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

class RegexStringMatchingStrategy implements StringMatchingStrategy {
    private final LoadingCache<String, Pattern> patternCache;

    RegexStringMatchingStrategy() {
        patternCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, Pattern>() {
                @Override
                public Pattern load(String key) {
                    return Pattern.compile(key);
                }
            });
    }

    @Override
    public boolean containsMatch(Set<String> matchExpressions, String metricName) {
        for (String regexExpression : matchExpressions) {
            final Pattern pattern = patternCache.getUnchecked(regexExpression);
            if (pattern != null && pattern.matcher(metricName).matches()) {
                // just need to match on a single value - return as soon as we do
                return true;
            }
        }
        return false;
    }
}
