package open.edx.qticonverter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.ZonedDateTime;

@ControllerAdvice
public class IOExceptionHandler {

    @ExceptionHandler(value = {IOException.class})
    public ResponseEntity<Object> handleIOException(IOException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        IOExceptionPayload ioExceptionPayload = new IOExceptionPayload(
                e.getMessage(),
                httpStatus,
                ZonedDateTime.now()
        );
        return new ResponseEntity<>(ioExceptionPayload, httpStatus);
    }

}
