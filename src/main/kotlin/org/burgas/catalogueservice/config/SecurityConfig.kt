package org.burgas.catalogueservice.config

import org.burgas.catalogueservice.service.IdentityDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    private final val passwordEncoder: PasswordEncoder
    private final val identityDetailsService: IdentityDetailsService

    constructor(passwordEncoder: PasswordEncoder, identityDetailsService: IdentityDetailsService) {
        this.passwordEncoder = passwordEncoder
        this.identityDetailsService = identityDetailsService
    }

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager {
        val manager = UserDetailsRepositoryReactiveAuthenticationManager(this.identityDetailsService)
        manager.setPasswordEncoder(this.passwordEncoder)
        return manager
    }

    @Bean
    fun corsConfiguration(): UrlBasedCorsConfigurationSource {
        val corsConfig = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "http://localhost:4200"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Content-Type", "Authorization")
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }
        return source
    }

    @Bean
    fun csrfConfiguration(): ServerCsrfTokenRequestAttributeHandler {
        val serverCsrfTokenRequestAttributeHandler = ServerCsrfTokenRequestAttributeHandler()
        serverCsrfTokenRequestAttributeHandler.setTokenFromMultipartDataEnabled(true)
        return serverCsrfTokenRequestAttributeHandler
    }

    @Bean
    fun securityWebFilterChain(httpSecurity: ServerHttpSecurity): SecurityWebFilterChain {
        return httpSecurity
            .cors { corsSpec -> corsSpec.configurationSource(this.corsConfiguration()) }
            .csrf { csrfSpec -> csrfSpec.csrfTokenRequestHandler(this.csrfConfiguration()) }
            .httpBasic { httpBasicSpec -> httpBasicSpec.authenticationManager(this.reactiveAuthenticationManager()) }
            .authorizeExchange { authorizeExchangeSpec -> authorizeExchangeSpec

                .pathMatchers(
                    "/api/v1/security/csrf-token",

                    "/api/v1/identities/create"
                )
                .permitAll()

                .pathMatchers(
                    "/api/v1/identities/by-id", "/api/v1/identities/update", "/api/v1/identities/delete",
                    "/api/v1/identities/change-password"
                )
                .hasAnyAuthority("ADMIN", "USER")

                .pathMatchers(
                    "/api/v1/identities", "/api/v1/identities/change-status"
                )
                .hasAnyAuthority("ADMIN")
            }
            .build()
    }
}