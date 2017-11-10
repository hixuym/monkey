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

package io.sunflower.resteasy.validation;

import com.fasterxml.classmate.*;
import com.fasterxml.classmate.members.RawMethod;
import com.fasterxml.classmate.members.ResolvedMethod;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.validation.GeneralValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author michael
 */
class HibernateGeneralValidator implements GeneralValidator {

    private static Logger logger = LoggerFactory.getLogger(HibernateGeneralValidator.class);

    private final Validator validator;
    private final boolean isExecutableValidationEnabled;
    private final ExecutableType[] defaultValidatedExecutableTypes;
    private TypeResolver typeResolver = new TypeResolver();

    public HibernateGeneralValidator(Validator validator,
                                     boolean isExecutableValidationEnabled,
                                     Set<ExecutableType> defaultValidatedExecutableTypes) {
        this.validator = validator;
        this.defaultValidatedExecutableTypes = defaultValidatedExecutableTypes.toArray(new ExecutableType[]{});
        this.isExecutableValidationEnabled = isExecutableValidationEnabled;
    }

    @Override
    public void validate(HttpRequest request, Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> cvs;

        try {
            cvs = validator.validate(object, groups);
        } catch (Exception e) {
            SimpleViolationsContainer violationsContainer = getViolationsContainer(request, object, null);
            violationsContainer.setException(e);
            throw new ResteasyViolationException(violationsContainer.getViolations(), object, null);
        }

        SimpleViolationsContainer violationsContainer = getViolationsContainer(request, object, null);
        violationsContainer.addViolations(cvs);

        if (violationsContainer.size() > 0) {
            throw new ResteasyViolationException(violationsContainer);
        }
    }

    @Override
    public void validateAllParameters(HttpRequest request, Object object, Method method, Object[] parameterValues, Class<?>... groups) {
        SimpleViolationsContainer violationsContainer = getViolationsContainer(request, object, null);

        violationsContainer.setMethod(method);

        if (method.getParameterTypes().length == 0) {
            checkViolations(request);
            return;
        }

        Set<ConstraintViolation<Object>> cvs;

        try {
            cvs = validator.forExecutables().validateParameters(object, method, parameterValues, groups);
        } catch (Exception e) {
            violationsContainer.setException(e);
            throw new ResteasyViolationException(violationsContainer.getViolations(), object, method);
        }

        violationsContainer.addViolations(cvs);

        if (violationsContainer.size() > 0) {
            throw new ResteasyViolationException(violationsContainer);
        }
    }

    @Override
    public void validateReturnValue(HttpRequest request, Object object, Method method, Object returnValue, Class<?>... groups) {
        SimpleViolationsContainer violationsContainer = getViolationsContainer(request, object, method);
        violationsContainer.setMethod(method);

        Set<ConstraintViolation<Object>> cvs;

        try {
            cvs = validator.forExecutables().validateReturnValue(object, method, returnValue, groups);
        } catch (Exception e) {
            violationsContainer.setException(e);
            throw new ResteasyViolationException(violationsContainer);
        }
        violationsContainer.addViolations(cvs);

        if (violationsContainer.size() > 0) {
            throw new ResteasyViolationException(violationsContainer);
        }
    }

    @Override
    public boolean isValidatable(Class<?> clazz) {
        return true;
    }

    @Override
    public boolean isMethodValidatable(Method method) {
        if (!isExecutableValidationEnabled) {
            return false;
        }

        ExecutableType[] types;
        List<ExecutableType[]> typesList = getExecutableTypesOnMethodInHierarchy(method);
        if (typesList.size() > 1) {
            throw new ValidationException("validateOnExceptionOnMultipleMethod");
        }
        if (typesList.size() == 1) {
            types = typesList.get(0);
        } else {
            ValidateOnExecution voe = method.getDeclaringClass().getAnnotation(ValidateOnExecution.class);
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

        boolean isGetterMethod = isGetter(method);
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

    @Override
    public void checkViolations(HttpRequest request) {
        // Called from resteasy-resteasy only if two argument version of isValidatable() returns true.
        SimpleViolationsContainer violationsContainer = getViolationsContainer(request, null, null);

        if (violationsContainer.size() > 0) {
            throw new ResteasyViolationException(violationsContainer.getViolations(), violationsContainer.getTarget(), violationsContainer.getMethod());
        }

    }

    protected SimpleViolationsContainer getViolationsContainer(HttpRequest request, Object target, Method method) {
        if (request == null) {
            return new SimpleViolationsContainer(target, method);
        }

        SimpleViolationsContainer violationsContainer = SimpleViolationsContainer.class.cast(request.getAttribute(SimpleViolationsContainer.class.getName()));
        if (violationsContainer == null) {
            violationsContainer = new SimpleViolationsContainer(target, method);
            request.setAttribute(SimpleViolationsContainer.class.getName(), violationsContainer);
        }

        return violationsContainer;
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
        String result = null;
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
                methods = AccessController.doPrivileged(new PrivilegedExceptionAction<Method[]>() {
                    @Override
                    public Method[] run() throws Exception {
                        return clazz.getDeclaredMethods();
                    }
                });
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
                resolvedMethods = AccessController.doPrivileged(new PrivilegedExceptionAction<ResolvedMethod[]>() {
                    @Override
                    public ResolvedMethod[] run() throws Exception {
                        return typeWithMembers.getMemberMethods();
                    }
                });
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
}
