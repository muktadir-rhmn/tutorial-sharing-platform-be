package tsp.be.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 409
    @ExceptionHandler(ValidationException.class)
    public Object handleConflict(HttpServletRequest request, Exception exception) {
        ValidationException validationException = (ValidationException) exception;
        return validationException.getErrorObject();
    }
}