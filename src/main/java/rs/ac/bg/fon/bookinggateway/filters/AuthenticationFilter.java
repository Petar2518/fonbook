package rs.ac.bg.fon.bookinggateway.filters;

import lombok.extern.slf4j.Slf4j;
import rs.ac.bg.fon.bookinggateway.config.RequestHeadersConfig;
import rs.ac.bg.fon.bookinggateway.util.ResponseManager;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RequestHeadersConfig headersConfig;
    private final ResponseManager responseManager;

    public AuthenticationFilter(RequestHeadersConfig headersConfig, ResponseManager responseManager) {
        super(Config.class);
        this.headersConfig = headersConfig;
        this.responseManager = responseManager;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            try {
                String token = Objects.requireNonNull(exchange.getRequest().getHeaders().get(headersConfig.getJwtHeader())).get(0);

                return WebClient.builder()
                        .filter(new LoggingFilter(AuthenticationFilter.class.getName()))
                        .build()
                        .get()
                        .uri(config.authenticationServiceHost + "/auth/validate-token")
                        .header(headersConfig.getJwtHeader(), token)
                        .exchange()
                        .flatMap(response -> responseManager.chainFiltersIfSuccessful(response, chain, exchange));

            } catch (NullPointerException e) {
                return responseManager.unauthorizedResponse(exchange);
            }

        };
    }

    public static class Config {
        private final String authenticationServiceHost;

        public Config(String authenticationServiceHost) {
            this.authenticationServiceHost = authenticationServiceHost;
        }
    }
}
