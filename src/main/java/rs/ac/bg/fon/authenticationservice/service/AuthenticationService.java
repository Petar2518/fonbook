package rs.ac.bg.fon.authenticationservice.service;

import rs.ac.bg.fon.authenticationservice.dto.request.AuthenticationRequestDto;
import rs.ac.bg.fon.authenticationservice.dto.response.AuthenticationResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

    AuthenticationResponseDto login(AuthenticationRequestDto authenticationRequest);

    void refreshToken(HttpServletRequest request, HttpServletResponse response);

}
