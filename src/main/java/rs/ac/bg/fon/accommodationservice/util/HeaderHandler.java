package rs.ac.bg.fon.accommodationservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.accommodationservice.exception.specific.MapperException;
import rs.ac.bg.fon.accommodationservice.model.JwtReceiver.UserInfo;

import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class HeaderHandler {

    private final ObjectMapper objectMapper;
    public UserInfo extractPayloadFromJwt(String jwt) {
        String jwtPayload = new String(Base64.getUrlDecoder().decode(jwt.split("\\.")[1]));
        try {
            return objectMapper.readValue(jwtPayload, UserInfo.class);
        }catch (Exception e){
            throw new MapperException(e);
        }

    }
}
