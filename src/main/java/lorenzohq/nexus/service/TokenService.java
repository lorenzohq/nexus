package lorenzohq.nexus.service;

import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.TokenPair;
import lorenzohq.nexus.entity.Token;
import lorenzohq.nexus.entity.User;
import lorenzohq.nexus.repository.TokenRepository;
import lorenzohq.nexus.security.AccessTokenService;
import lorenzohq.nexus.security.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final TokenRepository tokenRepository;

    @Value("${jwt.refresh-token-expiration-ms}")
    public long refreshTokenExpirationMs;

    public TokenPair issueAndSaveToken(User user) {
        String accessToken = accessTokenService.issueToken(user);
        String refreshToken = refreshTokenService.issueToken();
        String hashedRefreshToken = refreshTokenService.hashToken(refreshToken);

        Token t = new Token();
        t.setUser(user);
        t.setRefreshToken(hashedRefreshToken);
        t.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));

        tokenRepository.save(t);

        return new TokenPair(accessToken, refreshToken);
    }
}
