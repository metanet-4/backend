package com.metanet.team4.log;

import java.time.LocalDateTime;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HttpServletRequest request;

    // 🔹 Controller + Service 계층 로그 기록용 포인트컷
    @Pointcut("execution(public * com.metanet.team4..service..*(..))")
    public void loggableMethods() {}

    // 🔹 요청이 들어올 때 로그 저장 (INSERT)
    @Before("loggableMethods()")
    public void logRequest() {
        String method = request.getMethod();
        String endpoint = request.getRequestURI();
        String queryParams = request.getQueryString();
        String fullEndpoint = (queryParams == null) ? endpoint : endpoint + "?" + queryParams;

        // DB에 로그 기록
        String sql = "INSERT INTO api_logs (id, method, endpoint, request_time) VALUES (like_seq.nextval, ?, ?, ?)";
        jdbcTemplate.update(sql, method, fullEndpoint, LocalDateTime.now());
    }

    // 🔹 요청 정상 종료 시 응답 상태 코드 200으로 업데이트 (UPDATE)
    @AfterReturning(value = "loggableMethods()", returning = "result")
    public void logResponse(Object result) {
        String method = request.getMethod();
        String endpoint = request.getRequestURI();

        String sql = """
            UPDATE api_logs 
            SET status_code = ? 
            WHERE endpoint = ? AND method = ? 
            AND request_time = (SELECT MAX(request_time) FROM api_logs WHERE endpoint = ? AND method = ?)
        """;
        jdbcTemplate.update(sql, 200, endpoint, method, endpoint, method);
    }

    // 🔹 예외 발생 시 상태 코드 500으로 업데이트 (UPDATE)
    @AfterThrowing(value = "loggableMethods()", throwing = "exception")
    public void logError(Exception exception) {
        String method = request.getMethod();
        String endpoint = request.getRequestURI();

        String sql = """
            UPDATE api_logs 
            SET status_code = ? 
            WHERE endpoint = ? AND method = ? 
            AND request_time = (SELECT MAX(request_time) FROM api_logs WHERE endpoint = ? AND method = ?)
        """;
        jdbcTemplate.update(sql, 500, endpoint, method, endpoint, method);
    }

    // 🔹 실행 시간 측정 (추가 기능)
    @Around("loggableMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();
        long executionTime = end - start;

        // 실행 시간 로그 기록
        String method = request.getMethod();
        String endpoint = request.getRequestURI();
        String sql = """
            UPDATE api_logs 
            SET execution_time = ? 
            WHERE endpoint = ? AND method = ? 
            AND request_time = (SELECT MAX(request_time) FROM api_logs WHERE endpoint = ? AND method = ?)
        """;
        jdbcTemplate.update(sql, executionTime, endpoint, method, endpoint, method);

        return result;
    }
}
