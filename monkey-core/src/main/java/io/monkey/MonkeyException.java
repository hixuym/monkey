package io.monkey;

/**
 *
 */
public class MonkeyException extends RuntimeException {
    public MonkeyException() {
        super();
    }

    public MonkeyException(String message) {
        super(message);
    }

    public MonkeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MonkeyException(Throwable cause) {
        super(cause);
    }

    protected MonkeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
