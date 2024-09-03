package rs.ac.bg.fon.authenticationservice.util;

import rs.ac.bg.fon.authenticationservice.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtConfig jwtConfig;
    @Value("${jwt.secret-key}")
    private String secretKey;


    public String issueToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(Date.from(
                        Instant.now().plus(jwtConfig.getExpirationTime(), SECONDS)
                ))
                .signWith(getSigningKey())
                .compact();
    }

    public String issueRefreshToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(Date.from(
                        Instant.now().plus(jwtConfig.getRefreshTokenExpirationTime(), SECONDS)
                ))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String getSubject(String jwt) {
        return getClaims(jwt).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getRole(String jwt) {
        Claims claims = getClaims(jwt);
        return (String) claims.get("role");
    }

    private LocalDateTime getIssueDate(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getIssuedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


    public boolean isTokenValid(String jwt, String username, LocalDateTime tokenRevokedLastAt) {
        return username.equals(getSubject(jwt)) && !isTokenExpired(jwt) && !isTokenRevoked(jwt, tokenRevokedLastAt);
    }

    private boolean isTokenExpired(String jwt) {
        return getClaims(jwt).getExpiration().before(new Date());
    }

    private boolean isTokenRevoked(String jwt, LocalDateTime tokenRevokedLastAt) {
        if (tokenRevokedLastAt == null) {
            return false;
        }

        LocalDateTime issueDate = getIssueDate(jwt);

        return issueDate.isBefore(tokenRevokedLastAt);
    }
}
