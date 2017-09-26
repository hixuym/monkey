/*
 * Copyright 2016 ninjaframework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sunflower.gizmo;

import java.io.Serializable;

/**
 * Functional interfaces for Gizmo controller methods accepting up to X number of arguments with type inference.
 */
public class ResourceMethods {

    /**
     * Marker interface that all functional interfaces will extend.  Useful for simple validation an object is a
     * ResourceMethod.
     */
    public interface ResourceMethod extends Serializable {
    }

    @FunctionalInterface
    public interface ResourceMethod0 extends ResourceMethod {
        Result apply() throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod1<A> extends ResourceMethod {
        Result apply(A a) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod2<A, B> extends ResourceMethod {
        Result apply(A a, B b) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod3<A, B, C> extends ResourceMethod {
        Result apply(A a, B b, C c) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod4<A, B, C, D> extends ResourceMethod {
        Result apply(A a, B b, C c, D d) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod5<A, B, C, D, E> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod6<A, B, C, D, E, F> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod7<A, B, C, D, E, F, G> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod8<A, B, C, D, E, F, G, H> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod9<A, B, C, D, E, F, G, H, I> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod10<A, B, C, D, E, F, G, H, I, J> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod11<A, B, C, D, E, F, G, H, I, J, K> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod12<A, B, C, D, E, F, G, H, I, J, K, L> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod13<A, B, C, D, E, F, G, H, I, J, K, L, M> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n) throws Exception;
    }

    @FunctionalInterface
    public interface ResourceMethod15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> extends ResourceMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o) throws Exception;
    }

    // if you need more than 15 arguments then we recommend using the
    // legacy Class, methodName strategy

    // helper methods to allow classes to accept `ResourceMethod` but still
    // have the compiler create the correct functional method under-the-hood

    static public ResourceMethod0 of(ResourceMethod0 functionalMethod) {
        return functionalMethod;
    }

    static public <A> ResourceMethod1<A> of(ResourceMethod1<A> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B> ResourceMethod2<A, B> of(ResourceMethod2<A, B> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C> ResourceMethod3<A, B, C> of(ResourceMethod3<A, B, C> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D> ResourceMethod4<A, B, C, D> of(ResourceMethod4<A, B, C, D> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E> ResourceMethod5<A, B, C, D, E> of(ResourceMethod5<A, B, C, D, E> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F> ResourceMethod6<A, B, C, D, E, F> of(ResourceMethod6<A, B, C, D, E, F> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G> ResourceMethod7<A, B, C, D, E, F, G> of(ResourceMethod7<A, B, C, D, E, F, G> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G, H> ResourceMethod8<A, B, C, D, E, F, G, H> of(ResourceMethod8<A, B, C, D, E, F, G, H> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G, H, I> ResourceMethod9<A, B, C, D, E, F, G, H, I> of(ResourceMethod9<A, B, C, D, E, F, G, H, I> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G, H, I, J> ResourceMethod10<A, B, C, D, E, F, G, H, I, J> of(ResourceMethod10<A, B, C, D, E, F, G, H, I, J> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G, H, I, J, K> ResourceMethod11<A, B, C, D, E, F, G, H, I, J, K> of(ResourceMethod11<A, B, C, D, E, F, G, H, I, J, K> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G, H, I, J, K, L> ResourceMethod12<A, B, C, D, E, F, G, H, I, J, K, L> of(ResourceMethod12<A, B, C, D, E, F, G, H, I, J, K, L> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G, H, I, J, K, L, M> ResourceMethod13<A, B, C, D, E, F, G, H, I, J, K, L, M> of(ResourceMethod13<A, B, C, D, E, F, G, H, I, J, K, L, M> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G, H, I, J, K, L, M, N> ResourceMethod14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> of(ResourceMethod14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> functionalMethod) {
        return functionalMethod;
    }

    static public <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> ResourceMethod15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> of(ResourceMethod15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> functionalMethod) {
        return functionalMethod;
    }

}