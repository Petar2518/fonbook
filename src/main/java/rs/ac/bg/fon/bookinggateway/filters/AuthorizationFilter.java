package rs.ac.bg.fon.bookinggateway.filters;

import rs.ac.bg.fon.bookinggateway.config.RequestHeadersConfig;
import rs.ac.bg.fon.bookinggateway.util.JwtUtil;
import rs.ac.bg.fon.bookinggateway.util.ResponseManager;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    private final JwtUtil jwtUtil;
    private final RequestHeadersConfig headersConfig;
    private final ResponseManager responseManager;

    public AuthorizationFilter(JwtUtil jwtUtil, RequestHeadersConfig headersConfig, ResponseManager responseManager) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.headersConfig = headersConfig;
        this.responseManager = responseManager;
    }

    @Override
    public GatewayFilter apply(AuthorizationFilter.Config config) {
        return (exchange, chain) -> {

            String token = Objects.requireNonNull(exchange.getRequest().getHeaders().get(headersConfig.getJwtHeader())).get(0);
            String role = jwtUtil.getFromJwt(token, "role");

            return Objects.equals(role, config.role) ? chain.filter(exchange) : responseManager.unauthorizedResponse(exchange);
        };
    }

    public static class Config {
        private final String role;

        public Config(String role) {
            this.role = role;
        }
    }
}
