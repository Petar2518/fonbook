package rs.ac.bg.fon.userservice.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    WebRequest webRequest;
    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleAllExceptions() {
        Exception e = new Exception("message");

        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleAllExceptions(e, webRequest);

        assertNotNull(response.getBody());
        assertEquals(response.getBody().getMessage(), "message");
        assertEquals(response.getBody().getDetails(), webRequest.getDescription(false));
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void handleArgumentNotValidException() {
        Exception e = new Exception("message");

        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleArgumentNotValidException(e, webRequest);

        assertNotNull(response.getBody());
        assertEquals(response.getBody().getMessage(), "message");
        assertEquals(response.getBody().getDetails(), webRequest.getDescription(false));
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleUserNotFoundException() {
        UserNotFoundException e = new UserNotFoundException("message");

        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleUserNotFoundException(e, webRequest);

        assertNotNull(response.getBody());
        assertEquals(response.getBody().getMessage(), "message");
        assertEquals(response.getBody().getDetails(), webRequest.getDescription(false));
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void handleUserExistsException() {
        UserExistsException e = new UserExistsException("message");

        ResponseEntity<ErrorDetails> response = globalExceptionHandler.handleUserExistsException(e, webRequest);

        assertNotNull(response.getBody());
        assertEquals(response.getBody().getMessage(), "message");
        assertEquals(response.getBody().getDetails(), webRequest.getDescription(false));
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

}