package rs.ac.bg.fon.reservationservice.util;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

import javax.sql.DataSource;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(RabbitListenerTestComponent.class)
@Tag("springboot")
public class IntegrationTestBase {


    @Autowired
    RabbitListenerTestComponent rabbitListener;


    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
        rabbitmq.start();
    }

    @BeforeEach
    void cleanBase(@Autowired EntityManager entityManager) {
        Session session = entityManager.unwrap(Session.class);
        session.getSessionFactory().getSchemaManager().truncateMappedObjects();
    }

    @AfterEach
    public void execute() {
        rabbitListener.clearList();
    }


    @Container
    static final RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3");


    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("reservation_test_container")
                    .withUsername("username")
                    .withPassword("password");

    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername
        );

        registry.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword
        );

        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);

    }

    protected static DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .build();
    }


}