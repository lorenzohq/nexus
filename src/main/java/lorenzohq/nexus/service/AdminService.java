package lorenzohq.nexus.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.UpdateUserRoleDTO;
import lorenzohq.nexus.dto.UserDTO;
import lorenzohq.nexus.entity.User;
import lorenzohq.nexus.exception.NotFoundException;
import lorenzohq.nexus.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    private UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return toDTO(user);
    }

    @Transactional
    public UserDTO updateUserRole(UUID userId, @Valid UpdateUserRoleDTO body) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setRole(body.role());
        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }
}
