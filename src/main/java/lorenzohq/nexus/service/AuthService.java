package lorenzohq.nexus.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.RegisterBodyDTO;
import lorenzohq.nexus.dto.UserDTO;
import lorenzohq.nexus.entity.Role;
import lorenzohq.nexus.entity.User;
import lorenzohq.nexus.exception.ConflictException;
import lorenzohq.nexus.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO registerUser(@Valid RegisterBodyDTO body) {
        Optional<User> existingUser = userRepository.findByEmail(body.email());
        if (existingUser.isEmpty()) {
            throw new ConflictException("Email is taken");
        }

        User u = new User();
        u.setName(body.fullName());
        u.setEmail(body.email());
        u.setPassword(passwordEncoder.encode(body.password()));
        u.setRole(Role.USER);

        User createdUser = userRepository.save(u);

        return new UserDTO(
                createdUser.getId(),
                createdUser.getName(),
                createdUser.getEmail(),
                createdUser.getRole(),
                createdUser.getCreatedAt()
        );

    }
}
