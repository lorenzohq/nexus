package lorenzohq.nexus.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignTicketDTO(
        @NotNull
        UUID agentId
) {
}
