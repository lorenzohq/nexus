package lorenzohq.nexus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse er = new ErrorResponse(
                false,
                HttpStatus.BAD_REQUEST,
                message.isBlank() ? "Validation failed" : message,
                Instant.now()
        );
        return ResponseEntity.status(er.status()).body(er);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse er = new ErrorResponse(
                false,
                HttpStatus.BAD_REQUEST,
                "Malformed request body",
                Instant.now()
        );
        return ResponseEntity.status(er.status()).body(er);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse er = new ErrorResponse(
                false,
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action",
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
