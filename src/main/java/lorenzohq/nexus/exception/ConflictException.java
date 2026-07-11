package lorenzohq.nexus.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApplicationException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
