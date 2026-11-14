package io.github.kypleefearz.bodybuilding.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the public Auth0 settings to the frontend at runtime,
 * so the same Docker image works for every environment.
 */
@RestController
public class ClientConfigController {

    public record ClientConfig(boolean authEnabled, String authDomain, String authClientId, String authAudience) {
    }

    private final ClientConfig config;

    public ClientConfigController(
            @Value("${app.auth.domain}") String domain,
            @Value("${app.auth.client-id}") String clientId,
            @Value("${app.auth.audience}") String audience) {
        boolean enabled = !domain.isBlank() && !clientId.isBlank();
        this.config = new ClientConfig(enabled, domain, clientId, audience);
    }

    @GetMapping("/api/config")
    public ClientConfig config() {
        return config;
    }
}
