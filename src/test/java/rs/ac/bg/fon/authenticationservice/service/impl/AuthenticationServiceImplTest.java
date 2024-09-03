package rs.ac.bg.fon.authenticationservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.ac.bg.fon.authenticationservice.config.JwtConfig;
import rs.ac.bg.fon.authenticationservice.dto.response.RefreshTokenResponseDto;
import rs.ac.bg.fon.authenticationservice.model.Account;
import rs.ac.bg.fon.authenticationservice.model.Role;
import rs.ac.bg.fon.authenticationservice.repository.AccountRepository;
import rs.ac.bg.fon.authenticationservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    JwtConfig jwtConfig;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    PrintWriter printWriter;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void refreshToken() throws IOException {

        String refreshToken = "mockedRefreshToken";
        String email = "test@example.com";
        String expectedJsonResponse = "{\"accessToken\": \"testAccessToken\", \"refreshToken\": \"testRefreshToken\"}";


        Account account = new Account();
        account.setEmail(email);
        account.setRole(Role.USER);

        when(jwtConfig.getHeader()).thenReturn("Authorization");
        when(jwtConfig.getPrefix()).thenReturn("Bearer ");
        when(request.getHeader(anyString())).thenReturn("Bearer " + refreshToken);
        when(jwtUtil.getSubject(refreshToken)).thenReturn(email);
        when(accountRepository.getAccountByEmail(email)).thenReturn(Optional.of(account));
        when(jwtUtil.isTokenValid(refreshToken, email, null)).thenReturn(true);
        when(objectMapper.writeValueAsString(any(RefreshTokenResponseDto.class))).thenReturn(expectedJsonResponse);
        when(response.getWriter()).thenReturn(printWriter);


        authenticationService.refreshToken(request, response);

        verify(request, times(1)).getHeader(anyString());
        verify(jwtUtil, times(1)).getSubject(refreshToken);
        verify(accountRepository, times(1)).getAccountByEmail(email);
        verify(jwtUtil, times(1)).issueToken(email, Map.of(JwtConfig.ROLE_KEY, account.getRole().name()));
        verify(response).setContentType("application/json");

        ArgumentCaptor<String> jsonResponseBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(response.getWriter()).write(jsonResponseBodyCaptor.capture());
        String jsonResponseBody = jsonResponseBodyCaptor.getValue();

        assertThat(jsonResponseBody).isEqualTo(expectedJsonResponse);
    }
}