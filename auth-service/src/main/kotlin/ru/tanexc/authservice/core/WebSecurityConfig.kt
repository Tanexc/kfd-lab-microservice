package ru.tanexc.authservice.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    fun provideAuthEntryPoint(): AuthenticationEntryPoint = AuthEntryPoint()

    @Bean
    fun provideAuthTokenFilter(): AuthTokenFilter = AuthTokenFilter()

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.getAuthenticationManager()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .formLogin {
                it.disable()
            }
            .csrf { it.disable() }
            .cors { it.disable() }
            .httpBasic { it.disable() }
            .addFilterAfter(provideAuthTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/validate-token",
                ).permitAll()
            }
            .exceptionHandling { exception ->
                exception.authenticationEntryPoint(
                    provideAuthEntryPoint(),
                )
            }
            .build()
    }
}