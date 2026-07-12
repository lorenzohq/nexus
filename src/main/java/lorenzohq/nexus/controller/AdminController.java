package lorenzohq.nexus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.UpdateUserRoleDTO;
import lorenzohq.nexus.dto.UserDTO;
import lorenzohq.nexus.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleDTO body
    ) {
        return ResponseEntity.ok(adminService.updateUserRole(id, body));
    }
}
