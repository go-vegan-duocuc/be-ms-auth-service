package cl.govegan.msauthservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.service.jwt.JwtService;
import cl.govegan.msauthservice.service.login.LoginService;
import cl.govegan.msauthservice.service.register.RegisterService;
import cl.govegan.msauthservice.web.request.LoginRequest;
import cl.govegan.msauthservice.web.request.RegisterRequest;
import cl.govegan.msauthservice.web.response.ApiResponse;
import cl.govegan.msauthservice.web.response.TokenPayload;
import cl.govegan.msauthservice.web.response.UserPayload;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final RegisterService registerService;
    private final LoginService loginService;
    private final JwtService jwtService;

    @GetMapping("/status")
    public ResponseEntity<String> status () {
        return ResponseEntity.ok("Status ok");
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserPayload>>
    register (@RequestBody RegisterRequest registerRequest) {

        try {
            User user = registerService.register(registerRequest);
            return ResponseEntity.ok(ApiResponse.<UserPayload>builder()
                    .status(HttpStatus.OK.value())
                    .message("User registered successfully")
                    .data(UserPayload.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .build())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserPayload>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPayload>> login (@RequestBody LoginRequest loginRequest) {
        try {
            TokenPayload tokenPayload = loginService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.<TokenPayload>builder()
                    .status(HttpStatus.OK.value())
                    .message("User logged successfully")
                    .data(tokenPayload)
                    .build());
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(ApiResponse.<TokenPayload>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPayload> refreshToken (@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return ResponseEntity.ok(loginService.refresh(refreshToken));
    }

    @GetMapping("/secured-access")
    public ResponseEntity<String> securedAccess (Authentication authentication) {
        String jwt = authentication.getPrincipal().toString();

        return ResponseEntity.ok("Secured access granted for user: " + jwtService.extractSubject(jwt)+ " with userId: " + jwtService.extractUserId(jwt));
    }

    /* @PostMapping("/change-user")
    public ResponseEntity<ApiResponse<UserPayload>> changeUser(@RequestBody ChangeUserRequest changeUserRequest) {
        try {
            User user = userService.changeUser(changeUserRequest);
            return ResponseEntity.ok(ApiResponse.<UserPayload>builder()
                    .status(HttpStatus.OK.value())
                    .message("User changed successfully")
                    .data(UserPayload.builder()
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .build())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserPayload>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            userService.changePassword(changePasswordRequest);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Password changed successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build());
        }
    } */

}

