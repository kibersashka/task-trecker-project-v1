package com.task.tracker.authimpl.controller;

import com.task.tracker.authapi.dto.LoginResponse;
import com.task.tracker.authapi.dto.SignUpRequest;
import com.task.tracker.authapi.dto.SignUpResponse;
import com.task.tracker.authapi.dto.TokenCouple;
import com.task.tracker.authimpl.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";


    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam String username, @RequestParam String password) {
        log.info("login username: {}", username);
        TokenCouple tokenCouple = authService.login(username, password);
        System.out.println("tokenCouple: " + tokenCouple);
        return getLoginEntity(tokenCouple);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue(REFRESH_TOKEN_COOKIE_NAME) String requestRefreshToken) {
        log.info("Refresh token update request");
        TokenCouple tokenCouple = authService.refresh(requestRefreshToken);
        return getLoginEntity(tokenCouple);
    }

    @PostMapping("/register")
    public ResponseEntity<SignUpResponse> register(@RequestBody SignUpRequest registerRequest) {
        log.info("Account register request | username={}", registerRequest.username());
        SignUpResponse response = authService.signUp(registerRequest);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<LoginResponse> getLoginEntity(TokenCouple tokenCouple) {
        ResponseCookie responseCookie = ResponseCookie.from(
              REFRESH_TOKEN_COOKIE_NAME,
              tokenCouple.refreshToken()
        )
                .httpOnly(true)
                .path("/")
                .maxAge(tokenCouple.refreshTokenExpiration())
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new LoginResponse(tokenCouple.accessToken()));
    }
}
