package rs.ac.bg.fon.authenticationservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.authenticationservice.config.JwtConfig;
import rs.ac.bg.fon.authenticationservice.model.AccountUserDetails;
import rs.ac.bg.fon.authenticationservice.service.impl.AccountUserDetailsServiceImpl;
import rs.ac.bg.fon.authenticationservice.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccountUserDetailsServiceImpl accountUserDetailsService;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException, ExpiredJwtException {
        String authHeader = request.getHeader(jwtConfig.getHeader());

        if (authHeader == null || !authHeader.startsWith(jwtConfig.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String jwt = authHeader.substring(7);
            String subject = jwtUtil.getSubject(jwt);

            if (request.getRequestURI().equals("/auth/validate-token")) {
                response.setHeader("Role", jwtUtil.getRole(jwt));
            }

            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                AccountUserDetails userDetails = accountUserDetailsService.loadUserByUsername(subject);
                if (jwtUtil.isTokenValid(jwt, userDetails.getUsername(), userDetails.getTokenRevokedLastAt())) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(),
                            userDetails.getPassword(),
                            userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

        } catch (ExpiredJwtException ex) {
            LocalDateTime expirationTime = ex.getClaims().getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Map<String, Object> responseBody = new LinkedHashMap<>();
            responseBody.put("timestamp", LocalDateTime.now());
            responseBody.put("message", "JWT access token has expired at " + expirationTime);
            responseBody.put("details", "uri=" + request.getRequestURI());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
            return;
        }

        filterChain.doFilter(request, response);
    }
}

