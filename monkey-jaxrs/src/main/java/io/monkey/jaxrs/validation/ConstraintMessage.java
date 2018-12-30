package io.monkey.jaxrs.validation;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.sun.jndi.toolkit.url.Uri;
import io.monkey.validation.ValidationMethod;
import io.monkey.validation.selfvalidating.SelfValidating;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import javax.ws.rs.*;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ConstraintMessage {

    private static final Cache<Pair<Path, ? extends ConstraintDescriptor<?>>, String> PREFIX_CACHE =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build();

    private ConstraintMessage() {
    }

    /**
     * Gets the human friendly location of where the violation was raised.
     */
    public static String getMessage(ConstraintViolation<?> v, Method invocable) {
        final Pair<Path, ? extends ConstraintDescriptor<?>> of =
                Pair.of(v.getPropertyPath(), v.getConstraintDescriptor());
        final String cachePrefix = PREFIX_CACHE.getIfPresent(of);
        if (cachePrefix == null) {
            final String prefix = calculatePrefix(v, invocable);
            PREFIX_CACHE.put(of, prefix);
            return prefix + v.getMessage();
        }
        return cachePrefix + v.getMessage();
    }

    private static String calculatePrefix(ConstraintViolation<?> v, Method invocable) {
        final Optional<String> returnValueName = getMethodReturnValueName(v);
        if (returnValueName.isPresent()) {
            final String name = isValidationMethod(v) ?
                    StringUtils.substringBeforeLast(returnValueName.get(), ".") : returnValueName.get();
            return name + " ";
        }

        // Take the message specified in a ValidationMethod or SelfValidation
        // annotation if it is what caused the violation.
        if (isValidationMethod(v) || isSelfValidating(v)) {
            return "";
        }

        final Optional<String> entity = isRequestEntity(v, invocable);
        if (entity.isPresent()) {
            // A present entity means that the request body failed validation but
            // if the request entity is simple (eg. byte[], String, etc), the entity
            // string will be empty, so prepend a message about the request body
            final String prefix = Strings.isNullOrEmpty(entity.get()) ? "The request body" : entity.get();
            return prefix + " ";
        }

        // Check if the violation occurred on a *Param annotation and if so,
        // return a human friendly error (eg. "Query param xxx may not be null")
        final Optional<String> memberName = getMemberName(v, invocable);
        return memberName.map(s -> s + " ").orElseGet(() -> v.getPropertyPath() + " ");

    }

    /**
     * Determines if constraint violation occurred in the request entity. If it did, return a client
     * friendly string representation of where the error occurred (eg. "patient.name")
     */
    public static Optional<String> isRequestEntity(ConstraintViolation<?> violation, Method invocable) {

        final Path.Node parent = Iterables.get(violation.getPropertyPath(), 1, null);

        if (parent == null) {
            return Optional.empty();
        }

        final List<Parameter> parameters = Arrays.asList(invocable.getParameters());

        if (parent.getKind() == ElementKind.PARAMETER) {
            final Parameter param = parameters.get(parent.as(Path.ParameterNode.class).getParameterIndex());
            if (unknowParameterKind(param)) {
                return Optional.of(Joiner.on('.').join(Iterables.skip(violation.getPropertyPath(), 2)));
            }
        }

        return Optional.empty();
    }

    private static Set<Class<?>> annotations =
            ImmutableSet.of(
                    Context.class,
                    CookieParam.class,
                    FormParam.class,
                    HeaderParam.class,
                    MatrixParam.class,
                    PathParam.class,
                    QueryParam.class,
                    Suspended.class,
                    Uri.class,
                    BeanParam.class);

    private static boolean unknowParameterKind(Parameter param) {
        for (Annotation ann : param.getAnnotations()) {
            if (annotations.contains(ann.annotationType())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a method parameter (or a parameter field) name, if the violation raised in it.
     */
    private static Optional<String> getMemberName(ConstraintViolation<?> violation, Method invocable) {
        final int size = Iterables.size(violation.getPropertyPath());
        if (size < 2) {
            return Optional.empty();
        }

        final Path.Node parent = Iterables.get(violation.getPropertyPath(), size - 2);
        final Path.Node member = Iterables.getLast(violation.getPropertyPath());
        switch (parent.getKind()) {
            case PARAMETER:
                // Constraint violation most likely failed with a BeanParam
                final Parameter[] parameters = invocable.getParameters();
                final Parameter param = parameters[parent.as(Path.ParameterNode.class).getParameterIndex()];

                // Extract the failing *Param annotation inside the Bean Param
                if (param.isAnnotationPresent(BeanParam.class)) {
                    final Field field = FieldUtils.getField(param.getType(), member.getName(), true);
                    return ResteasyParameterNameProvider.getParameterNameFromAnnotations(field.getDeclaredAnnotations());
                }
                break;
            case METHOD:
                return Optional.of(member.getName());
            default:
                break;
        }
        return Optional.empty();
    }

    /**
     * Gets the method return value name, if the violation is raised in it
     */
    private static Optional<String> getMethodReturnValueName(ConstraintViolation<?> violation) {
        int returnValueNames = -1;

        final StringBuilder result = new StringBuilder("server response");
        for (Path.Node node : violation.getPropertyPath()) {
            if (node.getKind().equals(ElementKind.RETURN_VALUE)) {
                returnValueNames = 0;
            } else if (returnValueNames >= 0) {
                result.append(returnValueNames++ == 0 ? " " : ".").append(node);
            }
        }

        return returnValueNames >= 0 ? Optional.of(result.toString()) : Optional.empty();
    }

    private static boolean isValidationMethod(ConstraintViolation<?> v) {
        return v.getConstraintDescriptor().getAnnotation() instanceof ValidationMethod;
    }

    private static boolean isSelfValidating(ConstraintViolation<?> v) {
        return v.getConstraintDescriptor().getAnnotation() instanceof SelfValidating;
    }

    /**
     * Given a set of constraint violations and a Jersey {@link Method} where the constraint
     * occurred, determine the  HTTP Status code for the response. A return value violation is an
     * internal server error, an invalid request body is unprocessable entity, and any params that
     * are invalid means a bad request
     */
    public static <T extends ConstraintViolation<?>> int determineStatus(Set<T> violations, Method invocable) {
        if (violations.size() > 0) {
            final ConstraintViolation<?> violation = violations.iterator().next();
            for (Path.Node node : violation.getPropertyPath()) {
                switch (node.getKind()) {
                    case RETURN_VALUE:
                        return 500;
                    case PARAMETER:
                        // Now determine if the parameter is the request entity
                        final int index = node.as(Path.ParameterNode.class).getParameterIndex();
                        final Parameter parameter = invocable.getParameters()[index];
                        return unknowParameterKind(parameter) ? 422 : 400;
                    default:
                        continue;
                }
            }
        }

        // This shouldn't hit, but if it does, we'll return a unprocessable entity
        return 422;
    }
}
