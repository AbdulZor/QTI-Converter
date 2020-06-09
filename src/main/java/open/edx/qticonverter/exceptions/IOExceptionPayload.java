package open.edx.qticonverter.exceptions;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class IOExceptionPayload {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime zonedDateTime;

    public IOExceptionPayload(String message, HttpStatus httpStatus, ZonedDateTime zonedDateTime) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.zonedDateTime = zonedDateTime;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }
}
