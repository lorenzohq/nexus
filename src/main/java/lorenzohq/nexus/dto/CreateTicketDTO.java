package lorenzohq.nexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketDTO(
        @NotBlank
        @Size(max = 150, message = "Subject should be under 150 characters")
        String subject,

        @NotBlank
        @Size(max = 5000, message = "Description should be under 5000 characters")
        String description
) {
}
