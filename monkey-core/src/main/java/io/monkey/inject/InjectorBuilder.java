package io.monkey.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.Elements;
import com.google.inject.util.Modules;
import io.monkey.inject.spi.ModuleTransformer;
import io.monkey.inject.visitors.IsNotStaticInjectionVisitor;
import io.monkey.inject.visitors.KeyTracingVisitor;
import io.monkey.inject.visitors.WarnOfStaticInjectionVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * Simple DSL on top of Guice through which an injector may be created using a series
 * of operations and transformations of Guice modules.  Operations are tracked using a 
 * single module and are additive such that each operation executes on top of the entire 
 * current binding state.  Once all bindings have been defined the injector can be created 
 * <code>
 * InjectorBuilder
 *      .fromModule(new MyApplicationModule())
 *      .overrideWith(new OverridesForTesting())
 *      .traceEachElement(new BindingTracingVisitor())
 *      .createInjector();
 * </code>
 */
public final class InjectorBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(InjectorBuilder.class);
    
    private static final Stage LAZY_SINGLETONS_STAGE = Stage.DEVELOPMENT;
    
    private Module module;
    
    /**
     * Start the builder using the specified module. 
     * 
     * @param module
     * @return
     */
    public static InjectorBuilder fromModule(Module module) {
        return new InjectorBuilder(module);
    }
    
    public static InjectorBuilder fromModules(Module ... additionalModules) {
        return new InjectorBuilder(Modules.combine(additionalModules));
    }
    
    public static InjectorBuilder fromModules(List<Module> modules) {
        return new InjectorBuilder(Modules.combine(modules));
    }
    
    private InjectorBuilder(Module module) {
        this.module = module;
    }
    
    /**
     * Override all existing bindings with bindings in the provided modules.
     * This method uses Guice's build in {@link Modules#override} and is preferable
     * to using {@link Modules#override}.  The approach here is to attempt to promote 
     * the use of {@link Modules#override} as a single top level override.  Using
     * {@link Modules#override} inside Guice modules can result in duplicate bindings 
     * when the same module is installed in multiple placed. 
     * @param modules
     */
    public InjectorBuilder overrideWith(Module ... modules) {
        return overrideWith(Arrays.asList(modules));
    }
    
    /**
     * @see InjectorBuilder#overrideWith(Module...)
     */
    public InjectorBuilder overrideWith(Collection<Module> modules) {
        this.module = Modules.override(module).with(modules);
        return this;
    }
    
    /**
     * Add additional bindings to the module tracked by the DSL
     * @param modules
     */
    public InjectorBuilder combineWith(Module ... modules) {
        List<Module> m = new ArrayList<>();
        m.add(module);
        m.addAll(Arrays.asList(modules));
        this.module = Modules.combine(m);
        return this;
    }
    
    /**
     * Iterate through all elements of the current module and pass the output of the
     * ElementVisitor to the provided consumer.  'null' responses from the visitor are ignored.
     * 
     * This call will not modify any bindings
     * @param visitor
     */
    public <T> InjectorBuilder forEachElement(ElementVisitor<T> visitor, Consumer<T> consumer) {
        Elements
            .getElements(module)
            .forEach(element -> Optional.ofNullable(element.acceptVisitor(visitor)).ifPresent(consumer));
        return this;
    }

    /**
     * Call the provided visitor for all elements of the current module.
     * 
     * This call will not modify any bindings
     * @param visitor
     */
    public <T> InjectorBuilder forEachElement(ElementVisitor<T> visitor) {
        Elements
            .getElements(module)
            .forEach(element -> element.acceptVisitor(visitor));
        return this;
    }

    /**
     * Log the current binding state.  traceEachKey() is useful for debugging a sequence of
     * operation where the binding snapshot can be dumped to the log after an operation.
     */
    public InjectorBuilder traceEachKey() {
        return forEachElement(new KeyTracingVisitor(), LOG::debug);
    }
    
    /**
     * Log a warning that static injection is being used.  Static injection is considered a 'hack'
     * to alllow for backwards compatibility with non DI'd static code.
     */
    public InjectorBuilder warnOfStaticInjections() {
        return forEachElement(new WarnOfStaticInjectionVisitor(), LOG::debug);
    }
    
    /**
     * Extend the core DSL by providing a custom ModuleTransformer.  The output module 
     * replaces the current module.
     * @param transformer
     */
    public InjectorBuilder map(ModuleTransformer transformer) {
        this.module = transformer.transform(module);
        return this;
    }
    
    /**
     * Filter out elements for which the provided visitor returns true.
     * @param predicate
     */
    public InjectorBuilder filter(ElementVisitor<Boolean> predicate) {
        List<Element> elements = new ArrayList<>();
        for (Element element : Elements.getElements(Stage.TOOL, module)) {
            if (element.acceptVisitor(predicate)) {
                elements.add(element);
            }
        }
        this.module = Elements.getModule(elements);
        return this;
    }
    
    /**
     * Filter out all bindings using requestStaticInjection
     */
    public InjectorBuilder stripStaticInjections() {
        return filter(new IsNotStaticInjectionVisitor());
    }
    
    /**
     * @return Return all elements in the managed module
     */
    public List<Element> getElements() {
        return Elements.getElements(Stage.TOOL, module);
    }

    public Injector createInjector(Stage stage) {
        return Guice.createInjector(stage, module);
    }

    public Injector createInjector() {
        return Guice.createInjector(LAZY_SINGLETONS_STAGE, module);
    }

}
