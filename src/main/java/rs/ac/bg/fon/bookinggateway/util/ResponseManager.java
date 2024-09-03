package rs.ac.bg.fon.bookinggateway.util;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ResponseManager {

    public Mono<Void> chainFiltersIfSuccessful(ClientResponse response, GatewayFilterChain chain, ServerWebExchange exchange) {
        if (response.statusCode().is2xxSuccessful()) {
            return chain.filter(exchange);
        } else {
            return unauthorizedResponse(exchange);
        }
    }

    public Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

}
