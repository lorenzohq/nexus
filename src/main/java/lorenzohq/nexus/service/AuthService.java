package lorenzohq.nexus.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.*;
import lorenzohq.nexus.entity.Role;
import lorenzohq.nexus.entity.Token;
import lorenzohq.nexus.entity.User;
import lorenzohq.nexus.exception.ConflictException;
import lorenzohq.nexus.exception.UnauthorizedException;
import lorenzohq.nexus.repository.TokenRepository;
import lorenzohq.nexus.repository.UserRepository;
import lorenzohq.nexus.security.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;


    public UserDTO registerUser(@Valid RegisterBodyDTO body) {
        Optional<User> existingUser = userRepository.findByEmail(body.email());
        if (existingUser.isPresent()) {
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

    @Transactional
    public TokenPair login(@Valid LoginBodyDTO body) {
        User user = userRepository.findByEmail(body.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return tokenService.issueAndSaveToken(user);
    }

    @Transactional
    public TokenPair refresh(String refreshToken) {
        String hashedToken = refreshTokenService.hashToken(refreshToken);

        Token storedToken = tokenRepository.findByRefreshToken(hashedToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.getRevokedAt() != null || storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        storedToken.setRevokedAt(Instant.now());
        tokenRepository.save(storedToken);

        return tokenService.issueAndSaveToken(storedToken.getUser());
    }

    @Transactional
    public void logout(String refreshToken) {
        String hashedToken = refreshTokenService.hashToken(refreshToken);
        tokenRepository.findByRefreshToken(hashedToken).ifPresent(token -> {
            token.setRevokedAt(Instant.now());
            tokenRepository.save(token);
        });
    }
}
