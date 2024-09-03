package rs.ac.bg.fon.authenticationservice.util;

import rs.ac.bg.fon.authenticationservice.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("springboot")
@SpringBootTest
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    private String subject;
    private String token;

    @BeforeEach
    void setUp() {
        subject = "test@example.com";

        token = jwtUtil.issueToken(subject, Map.of("role", Role.USER.name()));
    }

    @Test
    void issueToken() {
        assertThat(token).isNotEmpty();
    }

    @Test
    void getSubject() {
        String retrievedSubject = jwtUtil.getSubject(token);
        assertThat(subject).isEqualTo(retrievedSubject);
    }

    @Test
    void getRole() {
        String retrievedRole = jwtUtil.getRole(token);
        assertThat(Role.USER.name()).isEqualTo(retrievedRole);
    }

    @Test
    void isTokenValid() {
        assertThat(jwtUtil.isTokenValid(token, subject, null)).isTrue();
    }

    @Test
    void isTokenValidTokenRevoked() {
        assertThat(jwtUtil.isTokenValid(token, subject, LocalDateTime.now().plusHours(1))).isFalse();
    }
}