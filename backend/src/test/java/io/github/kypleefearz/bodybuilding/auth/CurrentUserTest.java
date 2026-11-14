package io.github.kypleefearz.bodybuilding.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class CurrentUserTest {

    private static final String NS = "https://bodybuilding.app/";

    @Test
    void readsNamespacedClaimsAndAdminRole() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none")
                .subject("auth0|alice")
                .claim(NS + "name", "alice")
                .claim(NS + "email", "alice@example.com")
                .build();

        CurrentUser user = CurrentUser.from(jwt, List.of(new SimpleGrantedAuthority("ROLE_admin")), NS);

        assertThat(user).isEqualTo(new CurrentUser("auth0|alice", "alice", "alice@example.com", true));
    }

    @Test
    void fallsBackToStandardClaimsAndSubject() {
        Jwt withNickname = Jwt.withTokenValue("token").header("alg", "none")
                .subject("auth0|bob").claim("nickname", "bob").build();
        Jwt withoutName = Jwt.withTokenValue("token").header("alg", "none")
                .subject("auth0|carol").build();

        assertThat(CurrentUser.from(withNickname, List.of(), NS).username()).isEqualTo("bob");
        assertThat(CurrentUser.from(withoutName, List.of(), NS).username()).isEqualTo("auth0|carol");
        assertThat(CurrentUser.from(withoutName, List.of(), NS).admin()).isFalse();
    }

    @Test
    void ownersAndAdminsCanModerate() {
        CurrentUser alice = new CurrentUser("auth0|alice", "alice", null, false);
        CurrentUser admin = new CurrentUser("auth0|admin", "admin", null, true);

        assertThat(alice.canModerate("auth0|alice")).isTrue();
        assertThat(alice.canModerate("auth0|bob")).isFalse();
        assertThat(admin.canModerate("auth0|bob")).isTrue();
    }
}
