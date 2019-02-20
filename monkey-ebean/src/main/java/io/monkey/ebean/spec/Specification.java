/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.monkey.ebean.spec;

/**
 * Specificaiton interface.
 * Use {@link AbstractSpecification} as base for creating specifications, and
 * only the method {@link #isSatisfiedBy(Object)} must be implemented.
 *
 * @author Michael
 * Created at: 2019/2/18 15:55
 */
public interface Specification<T> {

    /**
     * Check if {@code t} is satisfied by the specification.
     *
     * @param t Object to test.
     * @return {@code true} if {@code t} satisfies the specification.
     */
    boolean isSatisfiedBy(T t);

    /**
     * Create a new specification that is the AND operation of {@code this} specification and another specification.
     *
     * @param specification Specification to AND.
     * @return A new specification.
     */
    Specification<T> and(Specification<T> specification);

    /**
     * Create a new specification that is the OR operation of {@code this} specification and another specification.
     *
     * @param specification Specification to OR.
     * @return A new specification.
     */
    Specification<T> or(Specification<T> specification);

    /**
     * Create a new specification that is the NOT operation of {@code this} specification.
     *
     * @param specification Specification to NOT.
     * @return A new specification.
     */
    Specification<T> not(Specification<T> specification);
}
