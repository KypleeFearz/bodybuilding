package io.github.kypleefearz.bodybuilding.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * The authenticated user of the current request, derived from the Auth0 access token.
 *
 * @param id       stable Auth0 subject, e.g. {@code auth0|64f...}
 * @param username display name shown in the forum
 * @param email    e-mail address, may be {@code null}
 * @param admin    whether the user has the {@code admin} role
 */
public record CurrentUser(String id, String username, String email, boolean admin) {

    public static final String ADMIN_AUTHORITY = "ROLE_admin";

    public static CurrentUser from(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String claimNamespace) {
        String email = firstNonBlank(jwt.getClaimAsString(claimNamespace + "email"), jwt.getClaimAsString("email"));
        String username = firstNonBlank(
                jwt.getClaimAsString(claimNamespace + "name"),
                jwt.getClaimAsString("nickname"),
                jwt.getClaimAsString("name"),
                email,
                jwt.getSubject());
        boolean admin = authorities.stream().anyMatch(a -> ADMIN_AUTHORITY.equals(a.getAuthority()));
        return new CurrentUser(jwt.getSubject(), username, email, admin);
    }

    /** Owners can always modify their own content, admins can moderate everything. */
    public boolean canModerate(String ownerId) {
        return admin || id.equals(ownerId);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
