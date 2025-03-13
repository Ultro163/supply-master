package com.example.supplymaster.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования выполнения методов сервисных классов.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Логирование выполнения методов в пакете price service.
     *
     * @param joinPoint точка присоединения, представляющая метод
     * @return результат выполнения метода
     * @throws Throwable если возникает исключение во время выполнения метода
     */
    @Around("execution(* com.example.supplymaster.service.price.*.*(..))")
    public Object logPriceService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "PriceService");
    }

    /**
     * Логирование выполнения методов в пакете shipment service.
     *
     * @param joinPoint точка присоединения, представляющая метод
     * @return результат выполнения метода
     * @throws Throwable если возникает исключение во время выполнения метода
     */
    @Around("execution(* com.example.supplymaster.service.shipment.*.*(..))")
    public Object logShipmentService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "ShipmentService");
    }

    /**
     * Логирует начало и завершение выполнения метода, а также возможные ошибки.
     *
     * @param joinPoint   точка присоединения, представляющая метод
     * @param serviceName название сервиса, в котором выполняется метод
     * @return результат выполнения метода
     * @throws Throwable если возникает исключение во время выполнения метода
     */
    private Object logExecution(ProceedingJoinPoint joinPoint, String serviceName) throws Throwable {
        String operation = defineOperation(joinPoint.getSignature().getName());
        log.info("[{}] {} началось. Аргументы: {}", serviceName, operation, joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();
            log.info("[{}] {} успешно завершено. Результат: {}", serviceName, operation, result);
            return result;
        } catch (Exception ex) {
            log.error("[{}] Ошибка при {}. Исключение: {}", serviceName, operation, ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Определяет тип операции по названию метода.
     *
     * @param methodName название метода
     * @return строковое представление операции
     */
    private String defineOperation(String methodName) {
        if (methodName.startsWith("create")) return "Сохранение";
        if (methodName.startsWith("update")) return "Обновление";
        if (methodName.startsWith("delete")) return "Удаление";
        if (methodName.startsWith("get")) return "Получение данных";
        return "Вызов метода " + methodName;
    }
}