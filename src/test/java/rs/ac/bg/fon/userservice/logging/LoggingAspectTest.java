package rs.ac.bg.fon.userservice.logging;

import rs.ac.bg.fon.userservice.exception.UserExistsException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    JoinPoint joinPoint;
    @Mock
    ProceedingJoinPoint proceedingJoinPoint;
    @Mock
    MethodSignature methodSignature;
    @Mock
    Logger log;
    LoggingAspect loggingAspect = new LoggingAspect();
    @Captor
    ArgumentCaptor<String> logMessageCaptor;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {

        Field loggerField = loggingAspect.getClass().getDeclaredField("log");
        loggerField.setAccessible(true);
        loggerField.set(loggingAspect, log);

        when(methodSignature.getDeclaringTypeName()).thenReturn("rs.ac.bg.fon.userservice.service.UserServiceImpl");
        when(methodSignature.getName()).thenReturn("createUser");
    }

    @Test
    void logAfterThrowing() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        loggingAspect.logAfterThrowing(joinPoint, new UserExistsException("User exists"));

        verify(log).error(logMessageCaptor.capture());
        String loggedMessage = logMessageCaptor.getValue();
        assertEquals("Exception in rs.ac.bg.fon.userservice.service.UserServiceImpl.createUser() \n" +
                "class rs.ac.bg.fon.userservice.exception.UserExistsException: User exists", loggedMessage);
    }

    @Test
    void logAround() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new String[]{"args"});
        when(proceedingJoinPoint.proceed()).thenReturn("result");
        when(log.isDebugEnabled()).thenReturn(true);

        loggingAspect.logAround(proceedingJoinPoint);

        verify(log, times(2)).debug(logMessageCaptor.capture());

        List<String> loggedMessages = logMessageCaptor.getAllValues();
        assertEquals("Enter: rs.ac.bg.fon.userservice.service.UserServiceImpl.createUser() with arguments = [args]", loggedMessages.get(0));
        assertEquals("Exit: rs.ac.bg.fon.userservice.service.UserServiceImpl.createUser() with result = result", loggedMessages.get(1));
    }
}