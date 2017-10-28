package io.sunflower.extension;

/**
 * Created by michael on 17/9/2.
 */
public class ExtensionLoaderException extends RuntimeException {

    public ExtensionLoaderException() {
        super();
    }

    public ExtensionLoaderException(String message) {
        super(message);
    }

    public ExtensionLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionLoaderException(Throwable cause) {
        super(cause);
    }

    protected ExtensionLoaderException(String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
