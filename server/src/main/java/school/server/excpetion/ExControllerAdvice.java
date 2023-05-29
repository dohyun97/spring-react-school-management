package school.server.excpetion;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.server.dto.ErrorResult;
import school.server.excpetion.customExceptions.UnauthorizedException;

@RestControllerAdvice
public class ExControllerAdvice {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseEntity<ErrorResult> internalExHandle(Exception e){
        ErrorResult errorResult = new ErrorResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<>(errorResult,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResult> unAuthExHandle(Exception e){
        ErrorResult errorResult = new ErrorResult(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        return new ResponseEntity<>(errorResult,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResult> typeMatchHandler(InvalidFormatException e){
        ErrorResult errorResult = new ErrorResult(HttpStatus.BAD_REQUEST.value(), "Please type correct value");
        return new ResponseEntity<>(errorResult,HttpStatus.BAD_REQUEST);
    }
}
