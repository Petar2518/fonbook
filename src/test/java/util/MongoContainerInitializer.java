package util;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("springboot")
public abstract class MongoContainerInitializer {

    @Autowired
    private ApplicationContext applicationContext;

    @ClassRule
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",
                () -> "mongodb://" + mongoDBContainer.getHost() + ":" + mongoDBContainer.getFirstMappedPort());
    }

    @AfterEach
    void tearDown() {

        Map<String, ?> mongoRepositories = applicationContext.getBeansOfType(org.springframework.data.mongodb.repository.MongoRepository.class);

        mongoRepositories.forEach((beanName, repository) -> {
            if (repository instanceof org.springframework.data.mongodb.repository.MongoRepository) {
                ((org.springframework.data.mongodb.repository.MongoRepository<?, ?>) repository).deleteAll();
            }
        });
    }

}
