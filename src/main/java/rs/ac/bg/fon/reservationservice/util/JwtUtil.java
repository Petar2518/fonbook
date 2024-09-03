package rs.ac.bg.fon.reservationservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.reservationservice.model.ProfileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final ObjectMapper objectMapper;

    public ProfileInfo getFromJwt(String jwt) {
        String jwtPayload = new String(Base64.getUrlDecoder().decode(jwt.split("\\.")[1]));

        try {
            return objectMapper.readValue(jwtPayload, ProfileInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
