package rs.ac.bg.fon.bookinggateway.filters;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

public class LoggingFilter implements ExchangeFilterFunction {
    private final Logger log;

    public LoggingFilter(String className) {
        this.log = Logger.getLogger(className);
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        log.info(">> {" + request.method() + "} {" + request.url() + "}");
        log.info(request.headers().toString());

        return next.exchange(request)
                .doOnNext(response -> {
                    log.info("<< {" + response.statusCode().value() + "} {" + response.statusCode() + "}");
                    log.info(response.headers().asHttpHeaders().toString());
                });
    }
}
