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

package io.sunflower.guicey.spi;

import com.google.inject.Binder;
import com.google.inject.Key;

import java.lang.annotation.Annotation;

/**
 * @see ScanningModuleBuidler
 */
public interface AnnotatedClassScanner {
    /**
     * @return Annotation class handled by this scanner
     */
    Class<? extends Annotation> annotationClass();
    
    /**
     * Apply the found class on the provided binder.  This can result in 0 or more bindings being
     * created
     * 
     * @param binder The binder on which to create new bindings
     * @param annotation The found annotation
     * @param key Key for the found class
     */
    <T> void applyTo(Binder binder, Annotation annotation, Key<T> key);

}
