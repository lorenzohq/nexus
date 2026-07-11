package lorenzohq.nexus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterBodyDTO(
        @NotBlank
        @Size(max = 100, message = "Full name should be under 100 character")
        String fullName,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 character")
        String password
) {
}
