package io.sunflower;

/**
 *
 */
public class SunflowerException extends RuntimeException {
    public SunflowerException() {
        super();
    }

    public SunflowerException(String message) {
        super(message);
    }

    public SunflowerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SunflowerException(Throwable cause) {
        super(cause);
    }

    protected SunflowerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
