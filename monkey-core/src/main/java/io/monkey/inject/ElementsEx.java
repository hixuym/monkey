/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.inject;

import com.google.inject.*;
import com.google.inject.spi.*;

import java.lang.reflect.Modifier;
import java.util.*;

final class ElementsEx {

    /**
     * @param elements List of elements
     * @return List all Module classes that were involved in setting up bindings for the list of
     * Elements
     */
    public static List<String> getAllSourceModules(List<Element> elements) {
        List<String> names = new ArrayList<>();
        for (Element element : elements) {
            if (element.getSource().getClass().isAssignableFrom(ElementSource.class)) {
                ElementSource source = (ElementSource) element.getSource();
                names.addAll(source.getModuleClassNames());
            }
        }
        return names;
    }

    /**
     * Discover all unbound keys (excluding JIT enabled keys) that Guice will not be able to create.
     * This set of keys can then be fed into an autobinder
     *
     * @return Set of interfaces or abstract classes for which there is no binding
     */
    public static Set<Key<?>> getAllUnboundKeys(List<Element> elements) {
        final Set<Key<?>> boundKeys = new HashSet<>();
        for (Element element : elements) {
            element.acceptVisitor(new DefaultElementVisitor<Void>() {
                public <T> Void visit(Binding<T> binding) {
                    boundKeys.add(binding.getKey());
                    return null;
                }
            });
        }

        final Set<Key<?>> foundKeys = new HashSet<>();

        for (Element element : elements) {
            element.acceptVisitor(new DefaultElementVisitor<Void>() {
                @Override
                public <T> Void visit(Binding<T> binding) {
                    binding.acceptTargetVisitor(new DefaultBindingTargetVisitor<T, Void>() {
                        @Override
                        public Void visit(ProviderKeyBinding<? extends T> binding) {
                            addFoundKeys(getUnboundDirectDependencies(binding.getProviderKey().getTypeLiteral(),
                                    boundKeys));
                            return null;
                        }

                        @Override
                        public Void visit(LinkedKeyBinding<? extends T> binding) {
                            if (!boundKeys.contains(binding.getLinkedKey())) {
                                addFoundKeys(getUnboundDirectDependencies(binding.getLinkedKey().getTypeLiteral(),
                                        boundKeys));
                            }
                            return null;
                        }

                        @Override
                        public Void visit(UntargettedBinding<? extends T> binding) {
                            addFoundKeys(
                                    getUnboundDirectDependencies(binding.getKey().getTypeLiteral(), boundKeys));
                            return null;
                        }

                        @Override
                        public Void visit(ConstructorBinding<? extends T> binding) {
                            addFoundKeys(getUnboundDirectDependencies(binding.getInjectableMembers(), boundKeys));
                            addFoundKeys(getUnboundDirectDependencies(binding.getDependencies(), boundKeys));
                            return null;
                        }

                        @Override
                        public Void visit(ProviderBinding<? extends T> binding) {
                            addFoundKeys(getUnboundDirectDependencies(binding.getProvidedKey().getTypeLiteral(),
                                    boundKeys));
                            return null;
                        }

                        private void addFoundKeys(Set<Key<?>> keys) {
                            foundKeys.addAll(keys);
                        }
                    });
                    return null;
                }

                /**
                 * Visit a request to inject the instance fields and methods of an instance.
                 */
                @Override
                public Void visit(InjectionRequest<?> request) {
                    for (InjectionPoint ip : request.getInjectionPoints()) {
                        for (Dependency<?> dep : ip.getDependencies()) {
                            foundKeys
                                    .addAll(getUnboundDirectDependencies(dep.getKey().getTypeLiteral(), boundKeys));
                        }
                    }
                    return null;
                }

                /**
                 * Visit a request to inject the static fields and methods of type.
                 */
                @Override
                public Void visit(StaticInjectionRequest request) {
                    for (InjectionPoint ip : request.getInjectionPoints()) {
                        for (Dependency<?> dep : ip.getDependencies()) {
                            foundKeys
                                    .addAll(getUnboundDirectDependencies(dep.getKey().getTypeLiteral(), boundKeys));
                        }
                    }
                    return null;
                }


                /**
                 * Visit a lookup of the provider for a type.
                 */
                @Override
                public <T> Void visit(ProviderLookup<T> lookup) {
                    foundKeys.add(lookup.getDependency().getKey());
                    return null;
                }
            });
        }

        // Recursively look at the final list of unbound keys to further discover dependencies
        // and exclude keys that may be instantiated using the JIT
        for (Key<?> key : foundKeys) {
            discoverDependencies(key, boundKeys);
        }
        foundKeys.removeAll(boundKeys);
        foundKeys.remove(Key.get(Injector.class));
        return foundKeys;
    }

    static void discoverDependencies(Key<?> key, Set<Key<?>> boundKeys) {
        if (boundKeys.contains(key)) {
            return;
        }
        Class<?> rawType = key.getTypeLiteral().getRawType();
        if (rawType.isInterface() || Modifier.isAbstract(rawType.getModifiers())) {
            ImplementedBy implementedBy = rawType.getAnnotation(ImplementedBy.class);
            if (implementedBy != null) {
                boundKeys.add(key);
                discoverDependencies(key, boundKeys);
                return;
            }

            ProvidedBy providedBy = rawType.getAnnotation(ProvidedBy.class);
            if (providedBy != null) {
                boundKeys.add(key);
                discoverDependencies(key, boundKeys);
                return;
            }
        } else {
            boundKeys.add(key);
            for (Key<?> dep : getUnboundDirectDependencies(key.getTypeLiteral(), boundKeys)) {
                discoverDependencies(dep, boundKeys);
            }
        }
    }

    static Set<Key<?>> getUnboundDirectDependencies(TypeLiteral<?> type, Set<Key<?>> boundKeys) {
        if (type.getRawType().isInterface()) {
            return Collections.emptySet();
        }
        Set<Key<?>> keys = new HashSet<>();
        keys.addAll(getUnboundDirectDependencies(InjectionPoint.forConstructorOf(type), boundKeys));
        keys.addAll(
                getUnboundDirectDependencies(InjectionPoint.forInstanceMethodsAndFields(type), boundKeys));
        return keys;
    }

    static Set<Key<?>> getUnboundDirectDependencies(Set<Dependency<?>> dependencies,
                                                    Set<Key<?>> boundKeys) {
        Set<Key<?>> unboundKeys = new HashSet<>();
        for (Dependency<?> dep : dependencies) {
            for (Dependency<?> dep2 : dep.getInjectionPoint().getDependencies()) {
                if (!boundKeys.contains(dep2.getKey())) {
                    unboundKeys.add(dep2.getKey());
                }
            }
        }
        return unboundKeys;
    }

    static Set<Key<?>> getUnboundDirectDependencies(InjectionPoint ip, Set<Key<?>> boundKeys) {
        Set<Key<?>> unboundKeys = new HashSet<>();
        for (Dependency<?> dep : ip.getDependencies()) {
            if (!boundKeys.contains(dep.getKey())) {
                unboundKeys.add(dep.getKey());
            }
        }
        return unboundKeys;
    }

    static Set<Key<?>> getUnboundDirectDependencies(Collection<InjectionPoint> ips,
                                                    Set<Key<?>> boundKeys) {
        Set<Key<?>> unboundKeys = new HashSet<>();
        for (InjectionPoint ip : ips) {
            for (Dependency<?> dep : ip.getDependencies()) {
                if (!boundKeys.contains(dep.getKey())) {
                    unboundKeys.add(dep.getKey());
                }
            }
        }
        return unboundKeys;
    }
}
