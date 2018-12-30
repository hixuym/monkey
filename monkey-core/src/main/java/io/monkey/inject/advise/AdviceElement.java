package io.monkey.inject.advise;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME) 
@BindingAnnotation
@interface AdviceElement {
    enum Type {
        SOURCE, ADVICE
    }
    
    /**
     * Unique ID that so multiple @Advice with the same return type may be defined without
     * resulting in a duplicate binding exception.
     */
    int uniqueId();
    
    /**
     * Name derived from a toString() of a qualifier and is used to match @Advice annotated method
     * with their @AdviceProvision
     */
    String name();
    
    Type type();
}