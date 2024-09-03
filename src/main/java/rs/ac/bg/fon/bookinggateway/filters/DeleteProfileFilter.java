package rs.ac.bg.fon.bookinggateway.filters;

import rs.ac.bg.fon.bookinggateway.config.RequestHeadersConfig;
import rs.ac.bg.fon.bookinggateway.util.ResponseManager;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Component
public class DeleteProfileFilter extends AbstractGatewayFilterFactory<DeleteProfileFilter.Config> {

    private final RequestHeadersConfig headersConfig;
    private final ResponseManager responseManager;

    public DeleteProfileFilter(RequestHeadersConfig headersConfig, ResponseManager responseManager) {
        super(Config.class);
        this.headersConfig = headersConfig;
        this.responseManager = responseManager;
    }

    @Override
    public GatewayFilter apply(DeleteProfileFilter.Config config) {
        return (exchange, chain) -> {

            try {
                String token = Objects.requireNonNull(exchange.getRequest().getHeaders().get(headersConfig.getJwtHeader())).get(0);
                String xsrf = Objects.requireNonNull(exchange.getRequest().getHeaders().get(headersConfig.getXsrfCookieHeader())).get(0);

                return WebClient.builder()
                        .filter(new LoggingFilter(DeleteProfileFilter.class.getName()))
                        .build()
                        .delete()
                        .uri(config.authenticationServiceHost + exchange.getRequest().getPath().toString().replace("users", "accounts").replace("hosts", "accounts"))
                        .cookie(headersConfig.getXsrfTokenHeader(), xsrf)
                        .header(headersConfig.getJwtHeader(), token)
                        .header(headersConfig.getXsrfCookieHeader(), xsrf)
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


