package io.sunflower.configuration;

import org.apache.commons.text.StrSubstitutor;

/**
 * A custom {@link StrSubstitutor} using environment variables as lookup source.
 */
public class EnvironmentVariableSubstitutor extends StrSubstitutor {

    public EnvironmentVariableSubstitutor() {
        this(true, false);
    }

    public EnvironmentVariableSubstitutor(boolean strict) {
        this(strict, false);
    }

    /**
     * @param strict                  {@code true} if looking up undefined environment variables should throw a {@link
     *                                UndefinedEnvironmentVariableException}, {@code false} otherwise.
     * @param substitutionInVariables a flag whether substitution is done in variable names.
     * @see EnvironmentVariableLookup#EnvironmentVariableLookup(boolean)
     * @see StrSubstitutor#setEnableSubstitutionInVariables(boolean)
     */
    public EnvironmentVariableSubstitutor(boolean strict, boolean substitutionInVariables) {
        super(new EnvironmentVariableLookup(strict));
        this.setEnableSubstitutionInVariables(substitutionInVariables);
    }
}
