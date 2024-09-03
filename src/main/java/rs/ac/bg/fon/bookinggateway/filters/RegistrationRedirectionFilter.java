package rs.ac.bg.fon.bookinggateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import rs.ac.bg.fon.bookinggateway.adapters.LocalDateTypeAdapter;
import rs.ac.bg.fon.bookinggateway.dto.HostDetailsDto;
import rs.ac.bg.fon.bookinggateway.dto.RegistrationDto;
import rs.ac.bg.fon.bookinggateway.dto.UserDetailsDto;
import rs.ac.bg.fon.bookinggateway.mapper.RegistrationRequestMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;


@Component
@Slf4j
public class RegistrationRedirectionFilter extends AbstractGatewayFilterFactory<RegistrationRedirectionFilter.Config> {

    private final RegistrationRequestMapper registrationMapper;

    public RegistrationRedirectionFilter(RegistrationRequestMapper registrationMapper) {
        super(Config.class);
        this.registrationMapper = registrationMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            if (exchange.getResponse().getStatusCode() != null && exchange.getResponse().getStatusCode().is2xxSuccessful()) {
                Long id = Long.valueOf(Objects.requireNonNull(exchange.getResponse().getHeaders().get("Id")).get(0));
                String requestString = Objects.requireNonNull(exchange.getAttribute("cachedRequestBodyObject")).toString();
                RegistrationDto registrationDto;
                try {
                    registrationDto = new ObjectMapper().readValue(requestString, RegistrationDto.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                String role = registrationDto.role();
                String body;
                String uri;
                try {
                    if (role.equals("USER")) {
                        UserDetailsDto userDetailsDto = registrationMapper.fromRegistrationToUser(registrationDto);
                        userDetailsDto.setUserId(id);
                        body = createJsonFromDto(userDetailsDto);
                        uri = config.userServiceHost + "/users";
                    } else {
                        HostDetailsDto hostDetailsDto = registrationMapper.fromRegistrationToHost(registrationDto);
                        hostDetailsDto.setId(id);
                        body = createJsonFromDto(hostDetailsDto);
                        uri = config.hostServiceHost + "/hosts";
                    }
                    HttpURLConnection connection = getConnection(uri, body);
                    try (BufferedReader ignored = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        connection.disconnect();
                    } catch (IOException e) {
                        rollbackRegistration(config.authenticationServiceHost + "/accounts/" + id);
                        return;
                    }
                } catch (IOException e) {
                    rollbackRegistration(config.authenticationServiceHost + "/accounts/" + id);
                    return;
                }
                if (!(setAuthenticationActive(config.authenticationServiceHost + "/accounts/activate/", id) && setUserOrHostActive(uri, id))) {
                    rollbackRegistration(config.authenticationServiceHost + "/accounts/" + id);
                    rollbackUserOrHost(uri + "/" + id);
                }
            }
        }));
    }

    private <T> String createJsonFromDto(T dto) {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .create()
                .toJson(dto);
    }

    private String readBody(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static HttpURLConnection getConnection(String uri, String body) throws IOException {
        HttpURLConnection connection;
        try {
            URL url = new URL(uri);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("content-type", "application/json; charset=UTF-8");

            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();

            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

        } catch (IOException e) {
            throw new IOException(e);
        }
        return connection;
    }

    private static void rollbackRegistration(String uri) {

        HttpURLConnection connection;
        try {
            URL url = new URL(uri);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty(
                    "Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            connection.getInputStream();
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void rollbackUserOrHost(String uri) {

        HttpURLConnection connection;
        try {
            URL url = new URL(uri);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty(
                    "Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            connection.getInputStream();
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean setAuthenticationActive(String uri, Long id) {
        HttpURLConnection connection;
        try {
            URL url = new URL(uri+id);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("content-type", "application/json; charset=UTF-8");

            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();

            byte[] input = BigInteger.valueOf(id).toByteArray();
            os.write(input, 0, input.length);
            if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static boolean setUserOrHostActive(String uri, Long id) {
        HttpURLConnection connection;
        try {
            URL url = new URL(uri+"/activate/" + id);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("content-type", "application/json; charset=UTF-8");

            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();

            byte[] input = BigInteger.valueOf(id).toByteArray();
            os.write(input, 0, input.length);
            if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

        } catch (IOException e) {

            return false;
        }

        return true;
    }


    public static class Config {
        private final String userServiceHost;
        private final String hostServiceHost;
        private final String authenticationServiceHost;

        public Config(String userServiceHost, String hostServiceHost, String authenticationServiceHost) {
            this.authenticationServiceHost = authenticationServiceHost;
            this.userServiceHost = userServiceHost;
            this.hostServiceHost = hostServiceHost;
        }
    }
}
