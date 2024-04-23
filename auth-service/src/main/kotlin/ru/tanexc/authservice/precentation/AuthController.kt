package ru.tanexc.authservice.precentation

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tanexc.authservice.data.model.ConnValidationResponse


@RestController
@RequestMapping("/auth")
class AuthController {

    @GetMapping("/validate-token")
    fun validateToken(request: HttpServletRequest): ResponseEntity<ConnValidationResponse> {
        val username = request.getAttribute("username") as String
        val grantedAuthorities: List<GrantedAuthority> = request.getAttribute("authorities") as List<GrantedAuthority>
        return ResponseEntity.ok(
            ConnValidationResponse(
                status = "OK",
                methodType = HttpMethod.GET.name(),
                username = username,
                authorities = grantedAuthorities,
                isAuthenticated = true
            )
        )
    }

}