package lorenzohq.nexus.dto;

import lorenzohq.nexus.entity.Priority;
import lorenzohq.nexus.entity.Status;

public record UpdateTicketDTO(
        Status status,
        Priority priority
) {
}
