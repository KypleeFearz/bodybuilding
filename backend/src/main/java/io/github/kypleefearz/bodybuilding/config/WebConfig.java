package io.github.kypleefearz.bodybuilding.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.kypleefearz.bodybuilding.auth.CurrentUserArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String claimNamespace;

    public WebConfig(@Value("${app.auth.claim-namespace}") String claimNamespace) {
        this.claimNamespace = claimNamespace;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(claimNamespace));
    }
}
