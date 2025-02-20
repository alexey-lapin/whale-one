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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

//    @Order(1)
//    @Bean
//    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
//                OAuth2AuthorizationServerConfigurer.authorizationServer();
//
//        http
//                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
//                .with(authorizationServerConfigurer, (authorizationServer) ->
//                        authorizationServer
//                                .oidc(Customizer.withDefaults())    // Enable OpenID Connect 1.0
//                )
//                .authorizeHttpRequests((authorize) ->
//                        authorize
//                                .anyRequest().authenticated()
//                )
//                // Redirect to the login page when not authenticated from the
//                // authorization endpoint
//                .exceptionHandling((exceptions) -> exceptions
//                        .defaultAuthenticationEntryPointFor(
//                                new LoginUrlAuthenticationEntryPoint("/login"),
//                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
//                        )
//                );
//
//        return http.build();
//    }

    @Order(2)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .oauth2ResourceServer(configurer -> {
                    configurer.jwt(c -> c.jwtAuthenticationConverter(new J()));
                })
                .csrf(AbstractHttpConfigurer::disable)
                .build();
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
    public NimbusJwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Component
    public static class J implements Converter<Jwt, AbstractAuthenticationToken> {

        private final JwtAuthenticationConverter delegate;

        public J() {
            this.delegate = new JwtAuthenticationConverter();
            var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            grantedAuthoritiesConverter.setAuthorityPrefix("");
            delegate.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        }

        @Override
        public AbstractAuthenticationToken convert(Jwt source) {
            AbstractAuthenticationToken convert = delegate.convert(source);
            return new JJ<>((AbstractOAuth2TokenAuthenticationToken<OAuth2Token>) convert);
        }
    }

    public static class JJ<T extends OAuth2Token> extends AbstractOAuth2TokenAuthenticationToken<T> {

        @Delegate(excludes = Ex.class)
        private final AbstractOAuth2TokenAuthenticationToken<T> delegate;

        public JJ(AbstractOAuth2TokenAuthenticationToken<T> delegate) {
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
