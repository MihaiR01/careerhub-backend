package ro.mihai.careerhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Login
                        .requestMatchers(
                                HttpMethod.POST,
                                "/login"
                        ).permitAll()

                        // Registration
                        .requestMatchers(
                                HttpMethod.POST,
                                "/users"
                        ).permitAll()

                        // Public jobs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/jobs",
                                "/jobs/{id}"
                        ).permitAll()

                        // Admin: create jobs and companies
                        .requestMatchers(
                                HttpMethod.POST,
                                "/jobs",
                                "/companies"
                        ).hasRole("ADMIN")

                        // Applications
                        .requestMatchers(
                                HttpMethod.POST,
                                "/applications"
                        ).hasRole("USER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/applications/*/status"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/applications/**"
                        ).authenticated()

                        // Users
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/users/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/users/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> roles =
                    jwt.getClaimAsStringList("roles");

            if (roles == null) {
                return List.of();
            }

            List<GrantedAuthority> authorities =
                    roles.stream()
                            .map(role -> (GrantedAuthority)
                                    new SimpleGrantedAuthority(role))
                            .toList();

            return authorities;
        });

        return converter;
    }
}