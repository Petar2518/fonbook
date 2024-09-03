package rs.ac.bg.fon.bookinggateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt-secret-key}")
    private String secretKey;

    public String getFromJwt(String jwt, String fieldName) {
        Claims claims = getClaims(jwt.substring(7));
        return (String) claims.get(fieldName);
    }
    private Claims getClaims(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
