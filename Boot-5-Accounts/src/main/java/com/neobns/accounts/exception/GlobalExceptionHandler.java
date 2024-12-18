package com.neobns.accounts.exception;

import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.neobns.accounts.dto.ErrorResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler  extends ResponseEntityExceptionHandler {

	private static final Logger errorlog = LoggerFactory.getLogger("ERROR");
	
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();

        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });
        MDC.put("executeResult", String.valueOf(status.value()));
        
        errorlog.info("[{}] [{} : {}] [{}] [{}]", MDC.get("requestId"), "ACCOUNTS", 
        		HttpStatus.INTERNAL_SERVER_ERROR.value(), "ACCOUNTS", "ACCOUNTS");

        clearMDC();
        
        System.out.println("재정의 메서드에서 에러 처리함");
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception,
                                                                            WebRequest webRequest, HttpServletRequest request) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now()
        );
        log.info("HandleGlobal 호출");
        setMDC(exception, HttpStatus.BAD_REQUEST, request);

        errorlog.info("[{}] [{} : {}] [{}] [{}]", MDC.get("requestId"), exception.getClass().getSimpleName(), 
        		HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getMethod(), request.getRequestURL().toString());

        clearMDC();
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                                 WebRequest webRequest, HttpServletRequest request) {
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                LocalDateTime.now()
        );
        log.info("ResourceNotFound 호출");
        setMDC(exception, HttpStatus.BAD_REQUEST, request);

        errorlog.info("[{}] [{} : {}] [{}] [{}]", MDC.get("requestId"), exception.getClass().getSimpleName(), 
        		HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getMethod(), request.getRequestURL().toString());

        clearMDC();
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomerAlreadyExistsException(CustomerAlreadyExistsException exception,
                                                                                 WebRequest webRequest, HttpServletRequest request){
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                LocalDateTime.now()
        );
        log.info("CustomerAlreadyExist 호출");
        setMDC(exception, HttpStatus.BAD_REQUEST, request);

        errorlog.info("[{}] [{} : {}] [{}] [{}]", MDC.get("requestId"), exception.getClass().getSimpleName(), 
        		HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getMethod(), request.getRequestURL().toString());

        clearMDC();
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }
    
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
    	log.info("ILLEGAL 핸들러 호출");
        setMDC(e, HttpStatus.BAD_REQUEST, request);

        errorlog.info("[{}] [{} : {}] [{}] [{}]", MDC.get("requestId"), e.getClass().getSimpleName(), 
        		HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getMethod(), request.getRequestURL().toString());

        clearMDC();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
    	log.info("런타입 핸들러 호출");
        setMDC(e, HttpStatus.INTERNAL_SERVER_ERROR, request);

        errorlog.info("[{}] [{} : {}] [{}] [{}]", MDC.get("requestId"), e.getClass().getSimpleName(), 
        		HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getMethod(), request.getRequestURL().toString());

        clearMDC();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
    }
    
    
    private void setMDC(Exception e, HttpStatus status, HttpServletRequest request) {
        MDC.put("executeResult", e.getClass().getSimpleName() + " : " + status.value());
        MDC.put("httpStatus", String.valueOf(status.value()));
        MDC.put("requestUri", request.getRequestURL().toString());
        MDC.put("httpMethod", request.getMethod());
        
     // 실제 에러 발생 위치 추출
        StackTraceElement callerLocation = getActualErrorLocation(e);
        MDC.put("className", callerLocation.getClassName());
        MDC.put("methodName", callerLocation.getMethodName());
    }

    private void clearMDC() {
        MDC.remove("executeResult");
        MDC.remove("httpStatus");
        MDC.remove("requestUri");
        MDC.remove("httpMethod");
        MDC.remove("className");
        MDC.remove("methodName");
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
