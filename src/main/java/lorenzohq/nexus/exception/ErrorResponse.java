package lorenzohq.nexus.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse(
        boolean success,
        HttpStatus status,
        String message,
        Instant timestamp
) { }
