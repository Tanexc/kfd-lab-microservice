package ru.tanexc.roomservice.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.tanexc.roomservice.data.model.Role
import ru.tanexc.roomservice.data.model.User
import ru.tanexc.roomservice.data.model.buildRole
import ru.tanexc.userservice.presentation.exceptions.InvalidCredentials
import ru.tanexc.userservice.presentation.exceptions.NotFoundException
import ru.tanexc.userservice.presentation.exceptions.ServerError
import java.security.Key
import java.util.*
import javax.crypto.SecretKey
import kotlin.collections.HashMap

@Component
class JwtHelper(
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.audience}") private val jwtAudience: String,
    @Value("\${jwt.domain}") private val jwtDomain: String,
    @Value("\${jwt.accessExp}") private val accessExp: Long,
    @Value("\${jwt.refreshExp}") private val refreshExp: Long,
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
        claims: HashMap<String, Any?> = HashMap(),
        isAccessToken: Boolean = true,
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

    fun extractUserId(token: String?): Long {
        val claims = getClaims(token)?: throw InvalidCredentials()
        val id = claims["id"]?: throw NotFoundException()
        return id.toString().toLong()
    }

    fun extractUserRole(token: String?): Role {
        val claims = getClaims(token)?: throw ServerError()
        val role = claims["role"]?.toString()?: throw InvalidCredentials()
        return role.buildRole()
    }

    private fun String.prepareToken(): String = this.split(" ").lastOrNull() ?: this
}
