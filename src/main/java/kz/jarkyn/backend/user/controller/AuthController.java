package kz.jarkyn.backend.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.user.model.dto.AccessTokenResponse;
import kz.jarkyn.backend.user.model.dto.LoginRequest;
import kz.jarkyn.backend.user.model.dto.LoginResponse;
import kz.jarkyn.backend.user.service.AuthService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping(Api.Auth.PATH)
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ip
    ) {
        LoginResponse loginResponse = authService.login(
                request.getPhoneNumber(),
                request.getPassword(),
                ip,
                userAgent
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
                .httpOnly(true)
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(365))
                .sameSite("Strict").build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(loginResponse.getAccessToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        AccessTokenResponse accessToken = authService.refresh(refreshToken);
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout();

        // очистить cookie
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.noContent().build();
    }
}