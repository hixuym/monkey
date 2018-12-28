package io.sunflower.jaxrs.auth;

import com.google.inject.ImplementedBy;

import java.security.Principal;

/**
 * An interface for classes which authorize principal objects.
 *
 * @param <P> the type of principals
 */
@ImplementedBy(PermitAllAuthorizer.class)
public interface Authorizer<P extends Principal> {

    /**
     * Decides if access is granted for the given principal in the given role.
     *
     * @param principal a {@link Principal} object, representing a user
     * @param role a user role
     * @return {@code true}, if the access is granted, {@code false otherwise}
     */
    boolean authorize(P principal, String role);
}
