package lorenzohq.nexus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.RegisterBodyDTO;
import lorenzohq.nexus.dto.UserDTO;
import lorenzohq.nexus.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterBodyDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(body));
    }
}
