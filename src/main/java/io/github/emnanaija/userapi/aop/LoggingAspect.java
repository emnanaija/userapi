package io.github.emnanaija.userapi.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut pour tous les contrôleurs
    @Pointcut("execution(* io.github.emnanaija.userapi.controller.*.*(..))")
    public void controllerMethods() {}

    // Pointcut pour tous les services
    @Pointcut("execution(* io.github.emnanaija.userapi.service.*.*(..))")
    public void serviceMethods() {}

    // Pointcut pour le gestionnaire d'exceptions
    @Pointcut("execution(* io.github.emnanaija.userapi.exception.*.*(..))")
    public void exceptionHandlerMethods() {}

    // Pointcut combiné : contrôleurs, services et gestionnaire d'exceptions
    @Pointcut("controllerMethods() || serviceMethods() || exceptionHandlerMethods()")
    public void applicationMethods() {}

    @Around("applicationMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Log avant l'exécution
        logger.info(">>> Appel de méthode: {}.{}() avec arguments: {}", 
                className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            // Exécution de la méthode
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // Log après l'exécution réussie
            logger.info("<<< Méthode {}.{}() exécutée avec succès en {} ms. Résultat: {}", 
                    className, methodName, executionTime, result != null ? result.toString() : "null");

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            // Log en cas d'erreur
            logger.error("!!! Erreur dans {}.{}() après {} ms: {}", 
                    className, methodName, executionTime, e.getMessage(), e);

            throw e;
        }
    }
}


