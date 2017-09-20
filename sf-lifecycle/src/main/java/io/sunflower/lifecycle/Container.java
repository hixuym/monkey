package io.sunflower.lifecycle;

import java.util.Collection;

/**
 * A Container
 */
public interface Container {
    /* ------------------------------------------------------------ */

    /**
     * Add a bean.  If the bean is-a {@link Listener}, then also do an implicit {@link #addEventListener(Listener)}.
     *
     * @param o the bean object to add
     * @return true if the bean was added, false if it was already present
     */
    boolean addBean(Object o);

    /**
     * @return the list of beans known to this aggregate
     * @see #getBean(Class)
     */
    Collection<Object> getBeans();

    /**
     * @param clazz the class of the beans
     * @param <T>   the Bean type
     * @return the list of beans of the given class (or subclass)
     * @see #getBeans()
     */
    <T> Collection<T> getBeans(Class<T> clazz);

    /**
     * @param clazz the class of the bean
     * @param <T>   the Bean type
     * @return the first bean of a specific class (or subclass), or null if no such bean exist
     */
    <T> T getBean(Class<T> clazz);

    /**
     * Removes the given bean. If the bean is-a {@link Listener}, then also do an implicit {@link
     * #removeEventListener(Listener)}.
     *
     * @param o the bean to remove
     * @return whether the bean was removed
     */
    boolean removeBean(Object o);

    /**
     * Add an event listener.
     *
     * @param listener the listener to add
     * @see Container#addBean(Object)
     */
    void addEventListener(Listener listener);

    /**
     * Remove an event listener.
     *
     * @param listener the listener to remove
     * @see Container#removeBean(Object)
     */
    void removeEventListener(Listener listener);

    /**
     * Unmanages a bean already contained by this aggregate, so that it is not started/stopped/destroyed with this
     * aggregate.
     *
     * @param bean The bean to unmanage (must already have been added).
     */
    void unmanage(Object bean);

    /**
     * Manages a bean already contained by this aggregate, so that it is started/stopped/destroyed with this aggregate.
     *
     * @param bean The bean to manage (must already have been added).
     */
    void manage(Object bean);

    /**
     * Adds the given bean, explicitly managing it or not.
     *
     * @param o       The bean object to add
     * @param managed whether to managed the lifecycle of the bean
     * @return true if the bean was added, false if it was already present
     */
    boolean addBean(Object o, boolean managed);

    /**
     * A listener for Container events. If an added bean implements this interface it will receive the events for this
     * container.
     */
    interface Listener {
        void beanAdded(Container parent, Object child);

        void beanRemoved(Container parent, Object child);
    }

    /**
     * Inherited Listener. If an added bean implements this interface, then it will be added to all contained beans that
     * are themselves Containers
     */
    interface InheritedListener extends Listener {
    }
}
