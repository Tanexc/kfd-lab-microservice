package ru.tanexc.authservice.core

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.tanexc.authservice.data.model.User
import java.security.Key
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtHelper(
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.audience}") private val jwtAudience: String,
    @Value("\${jwt.domain}") private val jwtDomain: String,
    @Value("\${jwt.accessExp}") private val accessExp: Long,
    @Value("\${jwt.refreshExp}") private val refreshExp: Long
) {
    private var jwtSecretKey: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))

    fun isTokenValid(token: String?): Boolean {
        runCatching {
            Jwts.parser()
                .verifyWith(jwtSecretKey as SecretKey)
                .build()
                .parseSignedClaims(token?.prepareToken())
            return true
        }
        return false
    }

    fun isRefreshToken(token: String): Boolean {
        getClaims(token)?.let {
            return it["type"] == "refresh"
        }
        return false
    }

    fun createToken(
        userDetails: User,
        claims: HashMap<String, Any?>,
        isAccessToken: Boolean,
    ): String {
        val token =
            Jwts.builder()
                .claims(
                    claims +
                        hashMapOf(
                            "id" to userDetails.id,
                            "role" to userDetails.role,
                            "type" to if (isAccessToken) "access" else "refresh",
                        ),
                )
                .subject(userDetails.username)
                .issuedAt(Date())
                .expiration(Date(Date().time + if (isAccessToken) accessExp else refreshExp))
                .issuer(jwtDomain)
        token.audience().add(jwtAudience)
        return token.signWith(jwtSecretKey).compact()
    }

    fun getClaims(token: String?): Claims? {
        return runCatching {
            Jwts.parser()
                .verifyWith(jwtSecretKey as SecretKey)
                .build()
                .parseSignedClaims(token?.prepareToken())
                .payload
        }.getOrNull()
    }

    private fun String.prepareToken(): String = this.split(" ").lastOrNull() ?: this
}
