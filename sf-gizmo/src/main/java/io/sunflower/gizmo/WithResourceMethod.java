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

import io.sunflower.gizmo.ResourceMethods.ResourceMethod;
import io.sunflower.gizmo.ResourceMethods.ResourceMethod0;
import io.sunflower.gizmo.ResourceMethods.ResourceMethod1;
import io.sunflower.gizmo.ResourceMethods.ResourceMethod2;
import io.sunflower.gizmo.ResourceMethods.ResourceMethod4;
import io.sunflower.gizmo.ResourceMethods.ResourceMethod7;

/**
 * Interface that exposes multiple with methods that accept a large number of various argument combinations.
 *
 * @param <T> The result to return
 */
public interface WithResourceMethod<T> {

    T with(ResourceMethods.ResourceMethod resourceMethod);

    default T with(ResourceMethod0 resourceMethod0) {
        return with((ResourceMethods.ResourceMethod) resourceMethod0);
    }

    default <A> T with(ResourceMethod1<A> resourceMethod1) {
        return with((ResourceMethods.ResourceMethod) resourceMethod1);
    }

    default <A, B> T with(ResourceMethod2<A, B> resourceMethod2) {
        return with((ResourceMethods.ResourceMethod) resourceMethod2);
    }

    default <A, B, C> T with(ResourceMethods.ResourceMethod3<A, B, C> resourceMethod3) {
        return with((ResourceMethod) resourceMethod3);
    }

    default <A, B, C, D> T with(ResourceMethod4<A, B, C, D> resourceMethod4) {
        return with((ResourceMethods.ResourceMethod) resourceMethod4);
    }

    default <A, B, C, D, E> T with(ResourceMethods.ResourceMethod5<A, B, C, D, E> resourceMethod5) {
        return with((ResourceMethod) resourceMethod5);
    }

    default <A, B, C, D, E, F> T with(ResourceMethods.ResourceMethod6<A, B, C, D, E, F> resourceMethod6) {
        return with((ResourceMethods.ResourceMethod) resourceMethod6);
    }

    default <A, B, C, D, E, F, G> T with(ResourceMethod7<A, B, C, D, E, F, G> resourceMethod7) {
        return with((ResourceMethods.ResourceMethod) resourceMethod7);
    }

    default <A, B, C, D, E, F, G, H> T with(ResourceMethods.ResourceMethod8<A, B, C, D, E, F, G, H> resourceMethod8) {
        return with((ResourceMethod) resourceMethod8);
    }

    default <A, B, C, D, E, F, G, H, I> T with(ResourceMethods.ResourceMethod9<A, B, C, D, E, F, G, H, I> resourceMethod9) {
        return with((ResourceMethods.ResourceMethod) resourceMethod9);
    }

    default <A, B, C, D, E, F, G, H, I, J> T with(ResourceMethods.ResourceMethod10<A, B, C, D, E, F, G, H, I, J> resourceMethod10) {
        return with((ResourceMethods.ResourceMethod) resourceMethod10);
    }

}
