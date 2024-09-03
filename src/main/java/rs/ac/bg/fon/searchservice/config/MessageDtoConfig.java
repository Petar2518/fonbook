package rs.ac.bg.fon.searchservice.config;

import rs.ac.bg.fon.searchservice.util.IdHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "map")
@RequiredArgsConstructor
@Getter
@Setter
public class MessageDtoConfig {
    private final Map<String, Class<? extends IdHolder>> correspondingMessageDtoClassForString;
}
