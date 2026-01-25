package com.github.alexeylapin.whaleone.infrastructure.config;

import com.github.alexeylapin.whaleone.infrastructure.persistence.jdbc.UserJdbcRepository;
import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import com.github.alexeylapin.whaleone.infrastructure.security.JdbcUserDetailsManager;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(authz -> {
                    authz.requestMatchers("/error", "/api/auth/login").permitAll();
                    authz.requestMatchers(HttpMethod.GET, "/", "/index.html", "/assets/**").permitAll();
                    authz.requestMatchers(HttpMethod.GET,
                            "/login",
                            "/administration/**",
                            "/deployments/**",
                            "/equipment/**",
                            "/projects/**"
                    ).permitAll();
                    authz.requestMatchers(EndpointRequest.to(
                            HealthEndpoint.class,
                            InfoEndpoint.class
                    )).permitAll();
                    authz.anyRequest().authenticated();
                })
                .oauth2ResourceServer(configurer -> {
                    configurer.jwt(c -> c.jwtAuthenticationConverter(new ExtendedJwtAuthenticationConverter()));
                })
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public DefaultBearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver resolver = new DefaultBearerTokenResolver();
        resolver.setAllowUriQueryParameter(true);
        return resolver;
    }

    @Bean
    public UserDetailsManager userDetailsManager(UserJdbcRepository repository) {
        return new JdbcUserDetailsManager(repository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(@Value("${security.jwt.secret}") String secret) {
        byte[] bytes = Base64.getDecoder().decode(secret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "HmacSHA256");
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKeySpec).build();
        return new ImmutableJWKSet<>(new JWKSet(jwk));
    }

    @Bean
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public NimbusJwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    public static class ExtendedJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

        private final JwtAuthenticationConverter delegate;

        public ExtendedJwtAuthenticationConverter() {
            this.delegate = new JwtAuthenticationConverter();
            var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            grantedAuthoritiesConverter.setAuthorityPrefix("");
            delegate.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        }

        @SuppressWarnings("unchecked")
        @Override
        public AbstractAuthenticationToken convert(Jwt source) {
            AbstractAuthenticationToken convert = delegate.convert(source);
            return new IdUserJwtAuthenticationToken<>((AbstractOAuth2TokenAuthenticationToken<OAuth2Token>) convert);
        }
    }

    public static class IdUserJwtAuthenticationToken<T extends OAuth2Token> extends AbstractOAuth2TokenAuthenticationToken<T> {

        @Delegate(excludes = Ex.class)
        private final AbstractOAuth2TokenAuthenticationToken<T> delegate;

        public IdUserJwtAuthenticationToken(AbstractOAuth2TokenAuthenticationToken<T> delegate) {
            super(delegate.getToken());
            this.delegate = delegate;
        }

        @Override
        public Map<String, Object> getTokenAttributes() {
            return delegate.getTokenAttributes();
        }

        @Override
        public Object getPrincipal() {
            return new JwtIdUser((Jwt) super.getPrincipal());
        }

    }

    public interface Ex {
        OAuth2Token getToken();
    }

    public static class JwtIdUser implements IdUser {

        private final Jwt jwt;

        public JwtIdUser(Jwt jwt) {
            this.jwt = jwt;
        }

        @Override
        public long getId() {
            return jwt.getClaim("userId");
        }

        @Override
        public String getName() {
            return jwt.getSubject();
        }

        @Override
        public Object getDelegate() {
            return jwt;
        }

    }

}
