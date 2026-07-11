package lorenzohq.nexus.dto;

import lorenzohq.nexus.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String name,
        String email,
        Role role,
        Instant createdAt
) { }
