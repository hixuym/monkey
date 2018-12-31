package io.monkey.orm;

import io.ebean.BeanRepository;
import io.ebean.EbeanServer;

public abstract class AbstractRepository<ID, E> extends BeanRepository<ID, E> {

    /**
     * Create with the given bean type and EbeanServer instance.
     * <p>
     * Typically users would extend BeanRepository rather than BeanFinder.
     * </p>
     * <pre>
     *   {@code
     *
     *   @Inject
     *   public CustomerRepository(EbeanServer server) {
     *     super(Customer.class, server);
     *   }
     *
     * }</pre>
     *
     * @param type   The bean type
     * @param server The EbeanServer instance typically created via Guice factory or equivalent
     */
    protected AbstractRepository(Class<E> type, EbeanServer server) {
        super(type, server);
    }
}
