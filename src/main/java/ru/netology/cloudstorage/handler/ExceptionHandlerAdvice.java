package ru.netology.cloudstorage.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudstorage.dto.ExceptionDTO;
import ru.netology.cloudstorage.exception.ErrorDeleteFile;
import ru.netology.cloudstorage.exception.ErrorDownloadFile;
import ru.netology.cloudstorage.exception.ErrorGetFiles;
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
    @ExceptionHandler(ErrorDeleteFile.class)
    public ResponseEntity<ExceptionDTO> handleDeleteFileData(ErrorDeleteFile ex)
    {
        String errorMessage = String.format("%s %s", "Error delete file: ", ex.getMessage());
        ExceptionDTO errorResponse = new ExceptionDTO(errorMessage, 500);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(500));
    }
    @ExceptionHandler(ErrorDownloadFile.class)
    public ResponseEntity<ExceptionDTO> handleDownloadFileData(ErrorDownloadFile ex)
    {
        String errorMessage = String.format("%s %s", "Error download file: ", ex.getMessage());
        ExceptionDTO errorResponse = new ExceptionDTO(errorMessage, 500);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(500));
    }
    @ExceptionHandler(ErrorGetFiles.class)
    public ResponseEntity<ExceptionDTO> handleGetAllFiles(ErrorGetFiles ex)
    {
        String errorMessage = String.format("%s %s", "Error get files: ", ex.getMessage());
        ExceptionDTO errorResponse = new ExceptionDTO(errorMessage, 500);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(500));
    }
}
