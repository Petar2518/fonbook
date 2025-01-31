package rs.ac.bg.fon.bookinggateway.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "headers")
public class RequestHeadersConfig {
    private String jwtHeader;
    private String jwtPrefix;
    private String xsrfTokenHeader;
    private String xsrfCookieHeader;
}
