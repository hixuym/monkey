package io.sunflower.lifecycle;

/**
 * An implementation of the Managed Interface for {@link AutoCloseable} instances. Adding an {@code
 * AutoCloseableManager} instance to the application's environment ties that object’s lifecycle to
 * that of the application’s HTTP server. After the server has stopped (and after its graceful
 * shutdown period) the {@link #stop()} method is called, which will trigger the call to {@link
 * AutoCloseable#close()}
 * <p>
 * <p>Usage :</p>
 * <pre>
 * {@code
 * AutoCloseable client = ...;
 * AutoCloseableManager clientManager = new AutoCloseableManager(client);
 * environment.lifecycle().manage(clientManager);
 * }
 * </pre>
 */
public class AutoCloseableManager extends AbstractLifeCycle {

    private final AutoCloseable autoCloseable;

    /**
     * @param autoCloseable instance to close when the HTTP server stops.
     */
    public AutoCloseableManager(final AutoCloseable autoCloseable) {
        this.autoCloseable = autoCloseable;
    }

    /**
     * Calls {@link AutoCloseable#close()} given in the {@link AutoCloseableManager(AutoCloseable)}
     *
     * @throws Exception propagates {@link AutoCloseable#close()} exception
     */
    @Override
    public void doStop() throws Exception {
        this.autoCloseable.close();
    }
}
