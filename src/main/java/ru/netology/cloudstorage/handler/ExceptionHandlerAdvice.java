package ru.netology.cloudstorage.handler;

import io.minio.errors.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudstorage.dto.ExceptionDTO;
import ru.netology.cloudstorage.exception.ErrorInputData;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionDTO> handleValidCredentials(BadCredentialsException ex) {
        ExceptionDTO errorResponse = new ExceptionDTO(ex.getMessage(), 400);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(400));
    }
    @ExceptionHandler(ErrorInputData.class)
    public ResponseEntity<ExceptionDTO> handleInputData(ErrorInputData ex)
    {
        String errorMessage = String.format("%s %s", "Error input data: ", ex.getMessage());
        ExceptionDTO errorResponse = new ExceptionDTO(errorMessage, 400);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(400));
    }
}
