package com.example.neobns.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        setMDC(e, HttpStatus.BAD_REQUEST, request);

        log.error("IllegalArgumentException occurred - Class: {}, Message: {}, Status: {}, Request URI: {}",
                e.getClass().getName(), e.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), e);

        clearMDC();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        setMDC(e, HttpStatus.INTERNAL_SERVER_ERROR, request);

        log.error("RuntimeException occurred - Class: {}, Message: {}, Status: {}, Request URI: {}",
                e.getClass().getName(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI(), e);

        clearMDC();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e, HttpServletRequest request) {
        setMDC(e, HttpStatus.INTERNAL_SERVER_ERROR, request);

        log.error("Exception occurred - Class: {}, Message: {}, Status: {}, Request URI: {}",
                e.getClass().getName(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI(), e);
        clearMDC();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }

    
    private void setMDC(Exception e, HttpStatus status, HttpServletRequest request) {
        MDC.put("errorName", e.getClass().getSimpleName() + " : " + status.value());
        MDC.put("httpStatus", String.valueOf(status.value()));
        MDC.put("requestUri", request.getRequestURI());
        MDC.put("httpMethod", request.getMethod());
        
     // 실제 에러 발생 위치 추출
        StackTraceElement callerLocation = getActualErrorLocation(e);
        MDC.put("callerClass", callerLocation.getClassName());
        MDC.put("callerMethod", callerLocation.getMethodName());
    }

    private void clearMDC() {
        MDC.remove("errorName");
        MDC.remove("httpStatus");
        MDC.remove("requestUri");
        MDC.remove("httpMethod");
        MDC.remove("callerClass");
        MDC.remove("callerMethod");
    }
    
    private StackTraceElement getActualErrorLocation(Throwable e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            // 사용자 코드 패키지를 기준으로 필터링 
            if (element.getClassName().startsWith("com.example.neobns")) {
                return element;
            }
        }
        // 사용자 코드가 아닌 경우 첫 번째 요소 반환 클래스이름 / 메서드이름 / 파일이름 / 줄 번호
        return stackTrace.length > 0 ? stackTrace[0] : new StackTraceElement("UNKNOWN", "UNKNOWN", null, -1);
    }
    
}
