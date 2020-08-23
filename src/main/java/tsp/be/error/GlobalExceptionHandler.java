package tsp.be.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tsp.be.error.exceptions.ValidationException;
import tsp.be.utils.SingleMessageResponse;


import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //instead of creating a new SingleMessageResponse at each request, the response is cached.
    private SingleMessageResponse methodNotSupportedErrorResponse =  new SingleMessageResponse("Method Not Allowed");
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleMethodNotSupportedError(HttpServletRequest request, Exception exception) {
        return methodNotSupportedErrorResponse;
    }

    private SingleMessageResponse pathVariableTypeMismatchErrorResponse = new SingleMessageResponse("Path Variable Type Mismatch");
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handlePathVariableTypeMismatch(HttpServletRequest request, Exception exception) {
        return pathVariableTypeMismatchErrorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleRequestParameterMissing(HttpServletRequest request, Exception exception) {
        return new SingleMessageResponse(exception.getMessage());
    }

    private SingleMessageResponse messageNotReadableErrorResponse = new SingleMessageResponse("Invalid request body");
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleMessageNotReadableError(HttpServletRequest request, Exception exception) {
        return messageNotReadableErrorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Object handleValidationError(HttpServletRequest request, Exception exception) {
        ValidationException validationException = (ValidationException) exception;
        return validationException.getErrorObject();
    }

    private SingleMessageResponse internalServerErrorResponse = new SingleMessageResponse("Internal Server Error");
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Object handleInternalServerError(HttpServletRequest request, Exception exception) {
        String requestInfo = new StringBuilder("Server Error:: ").append(request.getMethod()).append(" ").append(request.getRequestURL()).toString();
        logger.error(requestInfo, exception);

        return internalServerErrorResponse;
    }
}