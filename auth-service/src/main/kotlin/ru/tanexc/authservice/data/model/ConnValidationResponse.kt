package ru.tanexc.authservice.data.model

import org.springframework.security.core.GrantedAuthority

data class ConnValidationResponse(
        private val status: String? = null,
        private val isAuthenticated: Boolean = false,
        private val methodType: String? = null,
        private val username: String? = null,
        private val authorities: List<GrantedAuthority>? = null,
    )