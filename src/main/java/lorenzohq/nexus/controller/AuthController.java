package lorenzohq.nexus.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lorenzohq.nexus.dto.*;
import lorenzohq.nexus.exception.UnauthorizedException;
import lorenzohq.nexus.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private ResponseCookie cookieBuilder(String name, String value, Duration maxAge, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("lax")
                .path(path)
                .maxAge(maxAge)
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterBodyDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(body));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginBodyDTO body) {
        TokenPair tokens = authService.login(body);

        ResponseCookie accessCookie = cookieBuilder("access_token", tokens.accessToken(), Duration.ofMinutes(15), "/");
        ResponseCookie refreshCookie = cookieBuilder("refresh_token", tokens.refreshToken(), Duration.ofDays(7), "/");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponseDTO(accessCookie.getValue()));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDTO> logout(HttpServletRequest request) {
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        authService.logout(refreshToken);
        ResponseCookie clearAccess = cookieBuilder("access_token", "", Duration.ZERO, "/");
        ResponseCookie clearRefresh = cookieBuilder("refresh_token", "", Duration.ZERO, "/");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                .body(new MessageDTO("User logged out"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(HttpServletRequest request) {
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        TokenPair tokens = authService.refresh(refreshToken);

        ResponseCookie accessCookie = cookieBuilder("access_token", tokens.accessToken(), Duration.ofMinutes(5), "/");
        ResponseCookie refreshCookie = cookieBuilder("refresh_token", tokens.refreshToken(), Duration.ofDays(7), "/");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponseDTO(accessCookie.getValue()));
    }
}
