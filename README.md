# authentication-service
The authentication-service is a Spring project dedicated to account authentication and authorization. It leverages JWT (JSON Web Token) for secure account authentication, facilitating safe data exchange between clients and servers. Authorization is role-based, allowing accounts access to specific system components based on their roles. Furthermore, accounts have the capability to change their passwords, enhancing overall security and access management.

Launching the service:

- cd authentication-service
- docker-compose up -d
- ./gradlew build
- ./gradlew bootRun

OpenAPI specification:

- http://localhost:8080/swagger-ui/index.html#/