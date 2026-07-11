package lorenzohq.nexus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        ErrorResponse er = new ErrorResponse(
                false,
                ex.getStatus(),
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(er.status()).body(er);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse er = new ErrorResponse(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(er.status()).body(er);
    }
}
