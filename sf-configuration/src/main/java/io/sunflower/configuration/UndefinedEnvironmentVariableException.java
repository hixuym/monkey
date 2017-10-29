package io.sunflower.configuration;

/**
 * @author michael
 */
public class UndefinedEnvironmentVariableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UndefinedEnvironmentVariableException(String errorMessage) {
        super(errorMessage);
    }
}
