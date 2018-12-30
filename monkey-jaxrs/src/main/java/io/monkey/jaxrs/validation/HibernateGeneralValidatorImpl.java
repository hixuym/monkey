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

package io.monkey.jaxrs.validation;

import com.fasterxml.classmate.*;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.google.common.collect.ImmutableList;
import io.monkey.validation.ConstraintViolations;
import io.monkey.validation.Validated;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;

/**
 * @author michael
 */
class HibernateGeneralValidatorImpl implements GeneralValidator {

    private static Logger logger = LoggerFactory.getLogger(HibernateGeneralValidatorImpl.class);

    /**
     * Used for resolving type parameters. Thread-safe.
     */
    private TypeResolver typeResolver = new TypeResolver();
    private final Validator validator;
    private boolean isExecutableValidationEnabled;
    private ExecutableType[] defaultValidatedExecutableTypes;

    public HibernateGeneralValidatorImpl(Validator validator,
                                         boolean isExecutableValidationEnabled,
                                         Set<ExecutableType> defaultValidatedExecutableTypes) {
        this.validator = validator;
        this.defaultValidatedExecutableTypes = defaultValidatedExecutableTypes.toArray(new ExecutableType[]{});
        this.isExecutableValidationEnabled = isExecutableValidationEnabled;
    }

    @Override
    public void validate(HttpRequest request, Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> cvs;

        ResourceInfo resourceInfo = ResteasyProviderFactory.getContextData(ResourceInfo.class);

        final Class<?>[] groupsInfo = getGroup(resourceInfo.getResourceMethod());

        final Set<ConstraintViolation<Object>> violations = new HashSet<>();
        final BeanDescriptor beanDescriptor = validator.getConstraintsForClass(resourceInfo.getClass());

        if (beanDescriptor.isBeanConstrained()) {
            violations.addAll(validator.validate(object, groups));
        }

        if (!violations.isEmpty()) {
            throw new ResteasyViolationException(violations, resourceInfo.getResourceMethod());
        }
    }

    @Override
    public void validateAllParameters(HttpRequest request, Object object, Method method, Object[] parameterValues, Class<?>... groups) {
        if (method.getParameterTypes().length == 0) {
            return;
        }

        final Class<?>[] groupsInfo = getGroup(method);

        final Set<ConstraintViolation<Object>> violations =
                validator.forExecutables().validateParameters(object, method, parameterValues, groupsInfo);

        if (!violations.isEmpty()) {
            throw new ResteasyViolationException(violations, method);
        }
    }

    @Override
    public void validateReturnValue(HttpRequest request, Object object, Method method, Object returnValue, Class<?>... groups) {
        // If the Validated annotation is on a method, then validate the response with
        // the specified constraint group.
        final Class<?>[] groupsInfo;
        if (method.isAnnotationPresent(Validated.class)) {
            groupsInfo = method.getAnnotation(Validated.class).value();
        } else {
            groupsInfo = new Class<?>[]{Default.class};
        }

        final Set<ConstraintViolation<Object>> violations =
                validator.forExecutables().validateReturnValue(object, method, returnValue, groupsInfo);

        if (!violations.isEmpty()) {
            logger.trace("Response validation failed: {}", ConstraintViolations.copyOf(violations));
            throw new ResteasyViolationException(violations, method);
        }
    }

    /**
     * If the request entity is annotated with {@link Validated} then run
     * validations in the specified constraint group else validate with the
     * {@link Default} group
     */
    private Class<?>[] getGroup(Method invocable) {
        final ImmutableList.Builder<Class<?>[]> builder = ImmutableList.builder();
        for (Parameter parameter : invocable.getParameters()) {
            if (parameter.isAnnotationPresent(Validated.class)) {
                builder.add(parameter.getAnnotation(Validated.class).value());
            }
        }

        final ImmutableList<Class<?>[]> groups = builder.build();
        switch (groups.size()) {
            // No parameters were annotated with Validated, so validate under the default group
            case 0:
                return new Class<?>[]{Default.class};

            // A single parameter was annotated with Validated, so use their group
            case 1:
                return groups.get(0);

            // Multiple parameters were annotated with Validated, so we must check if
            // all groups are equal to each other, if not, throw an exception because
            // the validator is unable to handle parameters validated under different
            // groups. If the parameters have the same group, we can grab the first
            // group.
            default:
                for (int i = 0; i < groups.size(); i++) {
                    for (int j = i; j < groups.size(); j++) {
                        if (!Arrays.deepEquals(groups.get(i), groups.get(j))) {
                            throw new WebApplicationException("Parameters must have the same validation groups in " +
                                    invocable.getName(), 500);
                        }
                    }
                }
                return groups.get(0);
        }
    }

    @Override
    public boolean isValidatable(Class<?> clazz) {
        final BeanDescriptor beanDescriptor = validator.getConstraintsForClass(clazz);
        return beanDescriptor.isBeanConstrained();
    }

    @Override
    public boolean isMethodValidatable(Method m) {
        if (!isExecutableValidationEnabled) {
            return false;
        }

        ExecutableType[] types;
        List<ExecutableType[]> typesList = getExecutableTypesOnMethodInHierarchy(m);
        if (typesList.size() > 1) {
            throw new ValidationException("validateOnExceptionOnMultipleMethod");
        }
        if (typesList.size() == 1) {
            types = typesList.get(0);
        } else {
            ValidateOnExecution voe = m.getDeclaringClass().getAnnotation(ValidateOnExecution.class);
            if (voe == null) {
                types = defaultValidatedExecutableTypes;
            } else {
                if (voe.type().length > 0) {
                    types = voe.type();
                } else {
                    types = defaultValidatedExecutableTypes;
                }
            }
        }

        boolean isGetterMethod = isGetter(m);
        for (int i = 0; i < types.length; i++) {
            switch (types[i]) {
                case IMPLICIT:
                case ALL:
                    return true;

                case NONE:
                    continue;

                case NON_GETTER_METHODS:
                    if (!isGetterMethod) {
                        return true;
                    }
                    continue;

                case GETTER_METHODS:
                    if (isGetterMethod) {
                        return true;
                    }
                    continue;

                default:
                    continue;
            }
        }
        return false;
    }


    protected List<ExecutableType[]> getExecutableTypesOnMethodInHierarchy(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        List<ExecutableType[]> typesList = new ArrayList<>();

        while (clazz != null) {
            // We start by examining the method itself.
            Method superMethod = getSuperMethod(method, clazz);
            if (superMethod != null) {
                ExecutableType[] types = getExecutableTypesOnMethod(superMethod);
                if (types != null) {
                    typesList.add(types);
                }
            }

            typesList.addAll(getExecutableTypesOnMethodInInterfaces(clazz, method));
            clazz = clazz.getSuperclass();
        }
        return typesList;
    }

    protected List<ExecutableType[]> getExecutableTypesOnMethodInInterfaces(Class<?> clazz, Method method) {
        List<ExecutableType[]> typesList = new ArrayList<>();
        Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Method interfaceMethod = getSuperMethod(method, interfaces[i]);
            if (interfaceMethod != null) {
                ExecutableType[] types = getExecutableTypesOnMethod(interfaceMethod);
                if (types != null) {
                    typesList.add(types);
                }
            }
            List<ExecutableType[]> superList = getExecutableTypesOnMethodInInterfaces(interfaces[i], method);
            if (superList.size() > 0) {
                typesList.addAll(superList);
            }
        }
        return typesList;
    }

    static protected ExecutableType[] getExecutableTypesOnMethod(Method method) {
        ValidateOnExecution voe = method.getAnnotation(ValidateOnExecution.class);
        if (voe == null || voe.type().length == 0) {
            return null;
        }
        ExecutableType[] types = voe.type();
        if (types == null || types.length == 0) {
            return null;
        }
        return types;
    }

    static protected boolean isGetter(Method m) {
        String name = m.getName();
        Class<?> returnType = m.getReturnType();
        if (returnType.equals(Void.class)) {
            return false;
        }
        if (m.getParameterTypes().length > 0) {
            return false;
        }
        if (name.startsWith("get")) {
            return true;
        }
        if (name.startsWith("is") && returnType.equals(boolean.class)) {
            return true;
        }
        return false;
    }

    static protected String convertArrayToString(Object o) {
        String result;
        if (o instanceof Object[]) {
            Object[] array = Object[].class.cast(o);
            StringBuffer sb = new StringBuffer("[").append(convertArrayToString(array[0]));
            for (int i = 1; i < array.length; i++) {
                sb.append(", ").append(convertArrayToString(array[i]));
            }
            sb.append("]");
            result = sb.toString();
        } else {
            result = (o == null ? "" : o.toString());
        }
        return result;
    }

    /**
     * Returns a super method, if any, of a method in a class.
     * Here, the "super" relationship is reflexive.  That is, a method
     * is a super method of itself.
     */
    protected Method getSuperMethod(Method method, final Class<?> clazz) {
        Method[] methods = new Method[0];
        try {
            if (System.getSecurityManager() == null) {
                methods = clazz.getDeclaredMethods();
            } else {
                methods = AccessController.doPrivileged(
                        (PrivilegedExceptionAction<Method[]>) clazz::getDeclaredMethods);
            }
        } catch (PrivilegedActionException pae) {

        }

        for (int i = 0; i < methods.length; i++) {
            if (overrides(method, methods[i])) {
                return methods[i];
            }
        }
        return null;
    }

    /**
     * Checks, whether {@code subTypeMethod} overrides {@code superTypeMethod}.
     * <p>
     * N.B. "Override" here is reflexive. I.e., a method overrides itself.
     *
     * @param subTypeMethod   The sub type method (cannot be {@code null}).
     * @param superTypeMethod The super type method (cannot be {@code null}).
     * @return Returns {@code true} if {@code subTypeMethod} overrides {@code superTypeMethod}, {@code false} otherwise.
     * <p>
     * Taken from Hibernate Validator
     */
    protected boolean overrides(Method subTypeMethod, Method superTypeMethod) {
        if (subTypeMethod == null || superTypeMethod == null) {
            throw new RuntimeException("expectTwoNonNullMethods");
        }

        if (!subTypeMethod.getName().equals(superTypeMethod.getName())) {
            return false;
        }

        if (subTypeMethod.getParameterTypes().length != superTypeMethod.getParameterTypes().length) {
            return false;
        }

        if (!superTypeMethod.getDeclaringClass().isAssignableFrom(subTypeMethod.getDeclaringClass())) {
            return false;
        }

        return parametersResolveToSameTypes(subTypeMethod, superTypeMethod);
    }

    /**
     * Taken from Hibernate Validator
     */
    protected boolean parametersResolveToSameTypes(Method subTypeMethod, Method superTypeMethod) {
        if (subTypeMethod.getParameterTypes().length == 0) {
            return true;
        }

        ResolvedType resolvedSubType = typeResolver.resolve(subTypeMethod.getDeclaringClass());
        MemberResolver memberResolver = new MemberResolver(typeResolver);
        memberResolver.setMethodFilter(new SimpleMethodFilter(subTypeMethod, superTypeMethod));
        final ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolvedSubType, null, null);
        ResolvedMethod[] resolvedMethods = new ResolvedMethod[0];
        try {
            if (System.getSecurityManager() == null) {
                resolvedMethods = typeWithMembers.getMemberMethods();
            } else {
                resolvedMethods = AccessController.doPrivileged((PrivilegedExceptionAction<ResolvedMethod[]>) typeWithMembers::getMemberMethods);
            }
        } catch (PrivilegedActionException pae) {

        }

        // The ClassMate doc says that overridden methods are flattened to one
        // resolved method. But that is the case only for methods without any
        // generic parameters.
        if (resolvedMethods.length == 1) {
            return true;
        }

        // For methods with generic parameters I have to compare the argument
        // types (which are resolved) of the two filtered member methods.
        for (int i = 0; i < resolvedMethods[0].getArgumentCount(); i++) {

            if (!resolvedMethods[0].getArgumentType(i).equals(resolvedMethods[1].getArgumentType(i))) {
                return false;
            }
        }

        return true;
    }


    /**
     * A filter implementation filtering methods matching given methods.
     *
     * @author Gunnar Morling
     * <p>
     * Taken from Hibernate Validator
     */
    static protected class SimpleMethodFilter implements Filter<RawMethod> {
        private final Method method1;
        private final Method method2;

        private SimpleMethodFilter(Method method1, Method method2) {
            this.method1 = method1;
            this.method2 = method2;
        }

        @Override
        public boolean include(RawMethod element) {
            return element.getRawMember().equals(method1) || element.getRawMember().equals(method2);
        }
    }

    @Override
    public void checkViolations(HttpRequest request) {
    }

//    protected Validator getValidator(HttpRequest request) {
//        Validator v = (Validator) request.getAttribute(Validator.class.getName());
//        if (v == null) {
//            Locale locale = getLocale(request);
//            if (locale == null) {
//                v = validatorFactory.getValidator();
//            } else {
//                MessageInterpolator interpolator = new LocaleSpecificMessageInterpolator(validatorFactory.getMessageInterpolator(), locale);
//                v = validatorFactory.usingContext().messageInterpolator(interpolator).getValidator();
//            }
//            request.setAttribute(Validator.class.getName(), v);
//        }
//        return v;
//    }

    static protected class LocaleSpecificMessageInterpolator implements MessageInterpolator {
        private final MessageInterpolator interpolator;
        private final Locale locale;

        public LocaleSpecificMessageInterpolator(MessageInterpolator interpolator, Locale locale) {
            this.interpolator = interpolator;
            this.locale = locale;
        }

        @Override
        public String interpolate(String messageTemplate, Context context) {
            return interpolator.interpolate(messageTemplate, context, locale);
        }

        @Override
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            return interpolator.interpolate(messageTemplate, context, locale);
        }
    }

    private Locale getLocale(HttpRequest request) {
        if (request == null) {
            return null;
        }
        List<Locale> locales = request.getHttpHeaders().getAcceptableLanguages();
        return locales == null || locales.isEmpty() ? null : locales.get(0);
    }

}
