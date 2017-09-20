package io.sunflower.guicey;

import com.google.inject.Binding;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.InternalInjectorCreator;
import com.google.inject.matcher.Matcher;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Provider;

/**
 * Created by michael on 17/9/15.
 */
public class Injectors {

    public static Throwable getFirstErrorFailure(CreationException e) {
        if (e.getErrorMessages().isEmpty()) {
            return e;
        }
        // return the first message that has root cause, probably an actual error
        for (Message message : e.getErrorMessages()) {
            if (message.getCause() != null) {
                return message.getCause();
            }
        }
        return e;
    }

    /**
     * Returns an instance of the given type with the {@link Named} annotation value. <p> This method allows you to
     * switch this code <code>injector.getInstance(Key.get(type, Names.named(name)));</code> <p> to the more concise
     * <code>Injectors.getInstance(injector, type, name);</code>
     */
    public static <T> T getInstance(Injector injector, Class<T> type, String name) {
        return injector.getInstance(Key.get(type, Names.named(name)));
    }

    /**
     * Returns a collection of all instances of the given base type
     *
     * @param baseClass the base type of objects required
     * @param <T>       the base type
     * @return a set of objects returned from this injector
     */
    public static <T> Set<T> getInstancesOf(Injector injector, Class<T> baseClass) {
        Set<T> answer = new HashSet<>();
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            Key<?> key = entry.getKey();
            Class<?> keyType = getKeyType(key);
            if (keyType != null && baseClass.isAssignableFrom(keyType)) {
                Binding<?> binding = entry.getValue();
                Object value = binding.getProvider().get();
                if (value != null) {
                    T castValue = baseClass.cast(value);
                    answer.add(castValue);
                }
            }
        }
        return answer;
    }

    /**
     * Returns a collection of all instances matching the given matcher
     *
     * @param matcher matches the types to return instances
     * @return a set of objects returned from this injector
     */
    public static <T> Set<T> getInstancesOf(Injector injector, Matcher<Class> matcher) {
        Set<T> answer = new HashSet<>();
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            Key<?> key = entry.getKey();
            Class<?> keyType = getKeyType(key);
            if (keyType != null && matcher.matches(keyType)) {
                Binding<?> binding = entry.getValue();
                Object value = binding.getProvider().get();
                answer.add((T) value);
            }
        }
        return answer;
    }

    /**
     * Returns a collection of all of the providers matching the given matcher
     *
     * @param matcher matches the types to return instances
     * @return a set of objects returned from this injector
     */
    public static <T> Set<Provider<T>> getProvidersOf(Injector injector, Matcher<Class> matcher) {
        Set<Provider<T>> answer = new HashSet<>();
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            Key<?> key = entry.getKey();
            Class<?> keyType = getKeyType(key);
            if (keyType != null && matcher.matches(keyType)) {
                Binding<?> binding = entry.getValue();
                answer.add((Provider<T>) binding.getProvider());
            }
        }
        return answer;
    }

    /**
     * Returns a collection of all providers of the given base type
     *
     * @param baseClass the base type of objects required
     * @param <T>       the base type
     * @return a set of objects returned from this injector
     */
    public static <T> Set<Provider<T>> getProvidersOf(Injector injector, Class<T> baseClass) {
        Set<Provider<T>> answer = new HashSet<>();
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            Key<?> key = entry.getKey();
            Class<?> keyType = getKeyType(key);
            if (keyType != null && baseClass.isAssignableFrom(keyType)) {
                Binding<?> binding = entry.getValue();
                answer.add((Provider<T>) binding.getProvider());
            }
        }
        return answer;
    }

    /**
     * Returns true if a binding exists for the given matcher
     */
    public static boolean hasBinding(Injector injector, Matcher<Class> matcher) {
        return !getBindingsOf(injector, matcher).isEmpty();
    }

    /**
     * Returns true if a binding exists for the given base class
     */
    public static boolean hasBinding(Injector injector, Class<?> baseClass) {
        return !getBindingsOf(injector, baseClass).isEmpty();
    }

    /**
     * Returns true if a binding exists for the given key
     */
    public static boolean hasBinding(Injector injector, Key<?> key) {
        Binding<?> binding = getBinding(injector, key);
        return binding != null;
    }

    /**
     * Returns the binding for the given key or null if there is no such binding
     */
    public static Binding<?> getBinding(Injector injector, Key<?> key) {
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        return bindings.get(key);
    }

    /**
     * Returns a collection of all of the bindings matching the given matcher
     *
     * @param matcher matches the types to return instances
     * @return a set of objects returned from this injector
     */
    public static Set<Binding<?>> getBindingsOf(Injector injector, Matcher<Class> matcher) {
        Set<Binding<?>> answer = new HashSet<>();
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            Key<?> key = entry.getKey();
            Class<?> keyType = getKeyType(key);
            if (keyType != null && matcher.matches(keyType)) {
                answer.add(entry.getValue());
            }
        }
        return answer;
    }

    /**
     * Returns a collection of all bindings of the given base type
     *
     * @param baseClass the base type of objects required
     * @return a set of objects returned from this injector
     */
    public static Set<Binding<?>> getBindingsOf(Injector injector, Class<?> baseClass) {
        Set<Binding<?>> answer = new HashSet<>();
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            Key<?> key = entry.getKey();
            Class<?> keyType = getKeyType(key);
            if (keyType != null && baseClass.isAssignableFrom(keyType)) {
                answer.add(entry.getValue());
            }
        }
        return answer;
    }

    /**
     * Returns the key type of the given key
     */
    public static <T> Class<?> getKeyType(Key<?> key) {
        Class<?> keyType = null;
        TypeLiteral<?> typeLiteral = key.getTypeLiteral();
        Type type = typeLiteral.getType();
        if (type instanceof Class) {
            keyType = (Class<?>) type;
        }
        return keyType;
    }

    /**
     * Creates an injector for the given set of services. This is equivalent to calling {@link #createInjector(Stage,
     * Module...)} with Stage.DEVELOPMENT.
     *
     * @throws CreationException if one or more errors occur during injector construction
     */
    public static Injector createInjector(Module... modules) {
        return createInjector(Arrays.asList(modules));
    }

    /**
     * Creates an injector for the given set of services. This is equivalent to calling {@link #createInjector(Stage,
     * Iterable)} with Stage.DEVELOPMENT.
     *
     * @throws CreationException if one or more errors occur during injector creation
     */
    public static Injector createInjector(Iterable<? extends Module> modules) {
        return createInjector(Stage.DEVELOPMENT, modules);
    }

    /**
     * Creates an injector for the given set of services, in a given development stage.
     *
     * @throws CreationException if one or more errors occur during injector creation.
     */
    public static Injector createInjector(Stage stage, Module... modules) {
        return createInjector(stage, Arrays.asList(modules));
    }

    /**
     * Creates an injector for the given set of services, in a given development stage.
     *
     * @throws CreationException if one or more errors occur during injector construction
     */
    public static Injector createInjector(Stage stage, Iterable<? extends Module> modules) {
        return new InternalInjectorCreator().stage(stage).addModules(modules).build();
    }

}
