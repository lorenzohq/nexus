package lorenzohq.nexus.dto;

import lorenzohq.nexus.entity.Priority;
import lorenzohq.nexus.entity.Status;

import java.time.Instant;
import java.util.UUID;

public record TicketDTO(
        UUID id,
        String subject,
        String description,
        Status status,
        Priority priority,
        UUID createdBy,
        String createdByName,
        UUID assignedTo,
        Instant createdAt
) {
}
