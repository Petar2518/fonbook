package rs.ac.bg.fon.searchservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "rabbitmq")
public record QueuesPropertiesConfig(
        Map<String, String> correspondingQueueNameForService,
        String accommodationExchangeRoutingKey,
        String accommodationExchangeName,
        String reservationExchangeRoutingKey,
        String reservationExchangeName
) {
}
