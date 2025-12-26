import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.enterprise.context.ApplicationScoped
import java.util.Date
import javax.crypto.SecretKey


@ApplicationScoped
open class JwtUtil {

    open val DEFAULT_EXPIRATION_MS = 1000 * 60 * 60 * 24 // 24 h
    open val JWT_SECRET = "your-secret-key-here-please-use-secure-key-in-production"

    open val signingKey: SecretKey
        get() = Keys.hmacShaKeyFor(JWT_SECRET.toByteArray(Charsets.UTF_8))


    open fun generateToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + DEFAULT_EXPIRATION_MS))
            .signWith(signingKey) // ← используем один и тот же ключ
            .compact()
    }

    open fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(signingKey) // ← тот же ключ
                .build()
                .parseSignedClaims(token)
            true
        } catch (ex: Exception) {
            false
        }
    }

    open fun getUsernameFromToken(token: String): String? {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (ex: Exception) {
            null
        }
    }
}