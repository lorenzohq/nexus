package lorenzohq.nexus.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
