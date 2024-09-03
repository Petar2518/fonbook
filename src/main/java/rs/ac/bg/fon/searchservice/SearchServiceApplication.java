package rs.ac.bg.fon.searchservice;

import rs.ac.bg.fon.searchservice.config.QueuesPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(QueuesPropertiesConfig.class)
public class SearchServiceApplication {

    public static void main(String[] args) {


        SpringApplication.run(SearchServiceApplication.class, args);
    }

}

