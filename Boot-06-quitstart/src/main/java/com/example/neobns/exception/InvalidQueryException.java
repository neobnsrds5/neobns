package com.example.neobns.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }
   public InvalidQueryException(String message) {
	   super(message);
   }
}
