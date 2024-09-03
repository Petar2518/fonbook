package rs.ac.bg.fon.userservice.logging;


import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Data
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
    }

    @Pointcut("within(rs.ac.bg.fon.userservice..*)" +
            " || within(rs.ac.bg.fon.userservice.service..*)" +
            " || within(rs.ac.bg.fon.userservice.controller..*)")
    public void applicationPackagePointcut() {
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in " + joinPoint.getSignature().getDeclaringTypeName() +
                "." + joinPoint.getSignature().getName() + "() \n"
                + e.getClass() + ": " + e.getMessage());
    }

    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Enter: " + joinPoint.getSignature().getDeclaringTypeName() +
                    "." + joinPoint.getSignature().getName() +
                    "() with arguments = " + Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Exit: " + joinPoint.getSignature().getDeclaringTypeName() +
                        "." + joinPoint.getSignature().getName() +
                        "() with result = " + result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: " + Arrays.toString(joinPoint.getArgs()) +
                    " in " + joinPoint.getSignature().getDeclaringTypeName() +
                    "." + joinPoint.getSignature().getName() + "()");
            throw e;
        }
    }
}
