package rs.ac.bg.fon.accommodationservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${api.title}") String apiTitle,
            @Value("${api.description}") String apiDescription,
            @Value("${api.version}") String apiVersion,
            @Value("${api.contact.name}") String apiContactName,
            @Value("${api.contact.email}") String apiContactEmail,
            @Value("${api.contact.url}") String apiContactUrl

    ) {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact()
                                .name(apiContactName)
                                .email(apiContactEmail)
                                .url(apiContactUrl))
                );
    }
}
