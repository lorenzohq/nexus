package lorenzohq.nexus.dto;

import jakarta.validation.constraints.NotNull;
import lorenzohq.nexus.entity.Role;

public record UpdateUserRoleDTO(
        @NotNull
        Role role
) {
}
