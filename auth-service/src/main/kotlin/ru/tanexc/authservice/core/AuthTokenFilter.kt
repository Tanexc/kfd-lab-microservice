package ru.tanexc.authservice.core

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.tanexc.authservice.data.repository.UserRepository
import java.io.IOException

@Component
class AuthTokenFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtHelper: JwtHelper

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val token = parseJwt(request)
            if (jwtHelper.isTokenValid(token)) {
                val username: String = jwtHelper.getClaims(token)?.subject ?: throw Exception("subject is null")
                val user = userRepository.findUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.authorities,
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            Companion.logger.error("Cannot set user authentication", e)
        }
        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String =
        runCatching {
            request
                .getHeader("Authorization")
                .split("Bearer ")[1]
        }.getOrDefault("")

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AuthTokenFilter::class.java)
    }
}
