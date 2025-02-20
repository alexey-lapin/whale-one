package com.github.alexeylapin.whaleone.infrastructure.web.api;

import com.github.alexeylapin.whaleone.infrastructure.security.IdUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.DefaultOAuth2AccessTokenResponseMapConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthApi {


    private final DefaultOAuth2AccessTokenResponseMapConverter convert = new DefaultOAuth2AccessTokenResponseMapConverter();
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtEncoder jwtEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        var authRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());

        var authResult = (UsernamePasswordAuthenticationToken) authenticationManager.authenticate(authRequest);
        var principal = (IdUser) authResult.getPrincipal();

        var scopes = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var tokenResponse = createTokenResponse(principal.getId(), principal.getName(), scopes);

        return convert.convert(tokenResponse);
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh(@AuthenticationPrincipal IdUser token) throws Exception {
        var jwt = (Jwt) token.getDelegate();

        var tokenResponse = createTokenResponse(token.getId(), jwt.getSubject(), jwt.getClaim(OAuth2ParameterNames.SCOPE));

        return convert.convert(tokenResponse);
    }

    private OAuth2AccessTokenResponse createTokenResponse(long userId, String subject, Collection<String> scopes) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(60);

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("userId", userId)
                .claim(OAuth2ParameterNames.SCOPE, scopes)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(header, accessTokenClaims)).getTokenValue();

        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.from(accessTokenClaims)
                .expiresAt(issuedAt.plusSeconds(180))
                .id(UUID.randomUUID().toString())
                .build();

        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(header, refreshTokenClaims)).getTokenValue();

        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(60)
                .refreshToken(refreshToken)
                .build();
    }

    public record LoginRequest(String username, String password) {
    }

}
